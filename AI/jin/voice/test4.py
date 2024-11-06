import os
import torch
import torchaudio
import torch.nn.functional as F
from transformers import Wav2Vec2FeatureExtractor, Wav2Vec2Model
import subprocess
import tempfile
import numpy as np
from scipy.signal import medfilt
from sklearn.preprocessing import StandardScaler
from collections import defaultdict
import random
import librosa
import warnings
warnings.filterwarnings('ignore')

# GPU 설정
os.environ["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID"
os.environ["CUDA_VISIBLE_DEVICES"] = "0"
device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

# 모델 초기화
model_name = "facebook/wav2vec2-large-robust-ft-swbd-300h"
model = Wav2Vec2Model.from_pretrained(model_name).to(device)
feature_extractor = Wav2Vec2FeatureExtractor.from_pretrained(model_name)
model.eval()

class AudioAugmenter:
    """오디오 데이터 증강 클래스"""
    def __init__(self):
        self.sample_rate = 16000
        
    def add_noise(self, waveform, noise_level=0.005):
        """배경 노이즈 추가"""
        noise = torch.randn_like(waveform) * noise_level
        return waveform + noise
    
    def change_speed(self, waveform, speed_factor):
        """재생 속도 변경"""
        if speed_factor == 1.0:
            return waveform
        
        length = len(waveform)
        new_length = int(length / speed_factor)
        
        # 리샘플링으로 속도 변경
        waveform = torch.from_numpy(
            librosa.effects.time_stretch(waveform.numpy(), rate=speed_factor)
        ).float()
        
        # 패딩 또는 잘라내기
        if len(waveform) < length:
            waveform = F.pad(waveform, (0, length - len(waveform)))
        else:
            waveform = waveform[:length]
            
        return waveform
    
    def change_pitch(self, waveform, pitch_steps):
        """피치 변경"""
        if pitch_steps == 0:
            return waveform
            
        y_shifted = librosa.effects.pitch_shift(
            waveform.numpy(), 
            sr=self.sample_rate,
            n_steps=pitch_steps
        )
        return torch.from_numpy(y_shifted).float()
    
    def apply_augmentation(self, waveform):
        """데이터 증강 적용"""
        augmented_waves = [waveform]  # 원본 포함
        
        # 노이즈 추가
        noise_levels = [0.001, 0.003, 0.005]
        for level in noise_levels:
            augmented_waves.append(self.add_noise(waveform, level))
        
        # 속도 변경
        speed_factors = [0.9, 1.1]
        for factor in speed_factors:
            augmented_waves.append(self.change_speed(waveform, factor))
        
        # 피치 변경
        pitch_steps = [-2, -1, 1, 2]
        for steps in pitch_steps:
            augmented_waves.append(self.change_pitch(waveform, steps))
        
        return augmented_waves

class AudioFeatureExtractor:
    """향상된 오디오 특징 추출 클래스"""
    def __init__(self, sample_rate=16000):
        self.sample_rate = sample_rate
        
    def extract_pitch(self, waveform):
        """기본 주파수(피치) 추출"""
        pitches, _ = librosa.piptrack(
            y=waveform.numpy(),
            sr=self.sample_rate,
            n_fft=2048,
            hop_length=512
        )
        return torch.from_numpy(np.mean(pitches, axis=1)).float()
    
    def extract_mfcc(self, waveform):
        """MFCC 특징 추출"""
        mfcc = librosa.feature.mfcc(
            y=waveform.numpy(),
            sr=self.sample_rate,
            n_mfcc=13
        )
        return torch.from_numpy(np.mean(mfcc, axis=1)).float()
    
    def extract_spectral_features(self, waveform):
        """스펙트럴 특징 추출"""
        spectral_centroids = librosa.feature.spectral_centroid(
            y=waveform.numpy(),
            sr=self.sample_rate
        )
        spectral_rolloff = librosa.feature.spectral_rolloff(
            y=waveform.numpy(),
            sr=self.sample_rate
        )
        return torch.cat([
            torch.from_numpy(np.mean(spectral_centroids, axis=1)).float(),
            torch.from_numpy(np.mean(spectral_rolloff, axis=1)).float()
        ])

class AudioPreprocessor:
    """향상된 오디오 전처리 클래스"""
    def __init__(self, target_sr=16000):
        self.target_sr = target_sr
        self.scaler = StandardScaler()
        self.augmenter = AudioAugmenter()
        self.feature_extractor = AudioFeatureExtractor(target_sr)
    
    def remove_silence(self, waveform, threshold=0.01):
        """묵음 구간 제거"""
        energy = torch.abs(waveform)
        mask = energy > threshold
        return waveform[mask]
    
    def apply_voice_activity_detection(self, waveform):
        """음성 구간 검출"""
        frame_length = 512
        hop_length = 256
        
        frames = torch.stack(torch.split(waveform, frame_length)[:-1])
        energy = torch.mean(frames.pow(2), dim=1)
        
        energy_smooth = torch.from_numpy(medfilt(energy.cpu().numpy(), kernel_size=5))
        threshold = torch.mean(energy_smooth) * 0.5
        
        vad_mask = energy_smooth > threshold
        
        return vad_mask

    def process_audio(self, waveform, sample_rate, apply_augmentation=False):
        """오디오 전처리 파이프라인"""
        # 모노로 변환
        if len(waveform.shape) > 1 and waveform.shape[0] > 1:
            waveform = torch.mean(waveform, dim=0, keepdim=True)
        
        # 리샘플링
        if sample_rate != self.target_sr:
            resampler = torchaudio.transforms.Resample(orig_freq=sample_rate, new_freq=self.target_sr)
            waveform = resampler(waveform)
        
        # 묵음 제거
        waveform = self.remove_silence(waveform.squeeze())
        
        # 정규화
        waveform = (waveform - waveform.mean()) / (waveform.std() + 1e-6)
        
        if apply_augmentation:
            return self.augmenter.apply_augmentation(waveform)
        
        return [waveform]

class SpeakerEmbeddingExtractor:
    """향상된 화자 임베딩 추출 클래스"""
    def __init__(self, model, feature_extractor, preprocessor):
        self.model = model
        self.feature_extractor = feature_extractor
        self.preprocessor = preprocessor
        
    def extract_embedding(self, waveform):
        """화자 임베딩 추출"""
        with torch.no_grad():
            # wav2vec 특징 추출
            inputs = self.feature_extractor(
                waveform.numpy(), 
                sampling_rate=16000,
                padding=True,
                return_tensors="pt"
            )
            inputs = {k: v.to(device) for k, v in inputs.items()}
            
            # 모델 출력
            outputs = self.model(**inputs)
            hidden_states = outputs.last_hidden_state
            
            # 통계적 특징 추출
            mean_embedding = hidden_states.mean(dim=1)
            std_embedding = hidden_states.std(dim=1)
            max_embedding = hidden_states.max(dim=1).values
            
            # 추가 특징 추출
            pitch_features = self.preprocessor.feature_extractor.extract_pitch(waveform)
            mfcc_features = self.preprocessor.feature_extractor.extract_mfcc(waveform)
            spectral_features = self.preprocessor.feature_extractor.extract_spectral_features(waveform)
            
            # 모든 특징 결합
            additional_features = torch.cat([
                pitch_features.to(device),
                mfcc_features.to(device),
                spectral_features.to(device)
            ]).unsqueeze(0)
            
            final_embedding = torch.cat([
                mean_embedding,
                std_embedding,
                max_embedding,
                additional_features
            ], dim=-1)
            
            # 정규화
            final_embedding = F.normalize(final_embedding, p=2, dim=1)
            
            return final_embedding

class SpeakerRecognitionSystem:
    """향상된 화자 인식 시스템"""
    def __init__(self):
        self.preprocessor = AudioPreprocessor()
        self.embedding_extractor = SpeakerEmbeddingExtractor(model, feature_extractor, self.preprocessor)
        self.speaker_profiles = defaultdict(list)
        self.speaker_stats = {}
        self.similarity_threshold = 0.75
    
    def add_speaker(self, speaker_name, audio_folder):
        """화자 프로필 등록"""
        print(f"\nRegistering speaker: {speaker_name}")
        embeddings = []
        embedding_stats = []
        
        for file_name in os.listdir(audio_folder):
            if file_name.endswith(('.wav', '.m4a')):
                file_path = os.path.join(audio_folder, file_name)
                try:
                    if file_name.endswith('.m4a'):
                        file_path = convert_to_wav(file_path)
                    
                    waveform, sample_rate = torchaudio.load(file_path)
                    processed_waves = self.preprocessor.process_audio(
                        waveform, 
                        sample_rate,
                        apply_augmentation=True
                    )
                    
                    for processed_audio in processed_waves:
                        segments = self._split_audio(processed_audio)
                        for segment in segments:
                            if len(segment) >= 16000:  # 최소 1초
                                embedding = self.embedding_extractor.extract_embedding(segment)
                                embeddings.append(embedding)
                                embedding_stats.append(embedding.cpu().numpy())
                    
                    print(f"Successfully processed: {file_name}")
                    
                except Exception as e:
                    print(f"Error processing {file_name}: {e}")
                finally:
                    if file_path.endswith('.wav') and 'tmp' in file_path:
                        os.remove(file_path)
        
        if embeddings:
            self.speaker_profiles[speaker_name] = torch.cat(embeddings, dim=0)
            
            # 통계 정보 계산
            embedding_stats = np.vstack(embedding_stats)
            self.speaker_stats[speaker_name] = {
                'mean': np.mean(embedding_stats, axis=0),
                'std': np.std(embedding_stats, axis=0),
                'range': (np.min(embedding_stats, axis=0), np.max(embedding_stats, axis=0))
            }
            
            print(f"Successfully registered speaker: {speaker_name}")
        else:
            print(f"Failed to register speaker: {speaker_name}")
    
    def _split_audio(self, waveform, segment_length=32000):
        """오디오 세그먼트 분할"""
        segments = []
        for i in range(0, len(waveform), segment_length):
            segment = waveform[i:i + segment_length]
            if len(segment) >= 16000:
                segments.append(segment)
        return segments
    
    def _calculate_score(self, similarities, speaker_name, test_embedding):
        """향상된 유사도 점수 계산"""
        max_similarity = similarities.max().item()
        
        # 통계 기반 검증
        test_embedding_np = test_embedding.cpu().numpy()
        stats = self.speaker_stats[speaker_name]
        
        # Z-score 기반 검증
        z_score = np.abs((test_embedding_np - stats['mean']) / (stats['std'] + 1e-6))
        stat_score = 1 - np.mean(z_score)
        
        # 범위 기반 검증
        range_min, range_max = stats['range']
        in_range = np.logical_and(
            test_embedding_np >= range_min,
            test_embedding_np <= range_max
        )
        range_score = np.mean(in_range)
        
        # 최종 점수 계산 (가중 평균)
        final_score = (0.5 * max_similarity) + (0.3 * stat_score) + (0.2 * range_score)
        return final_score
    
    def identify_speaker(self, audio_path):
        """화자 식별"""
        try:
            if audio_path.endswith('.m4a'):
                audio_path = convert_to_wav(audio_path)
            
            waveform, sample_rate = torchaudio.load(audio_path)
            processed_waves = self.preprocessor.process_audio(waveform, sample_rate)
            
            segment_predictions = []
            
            for processed_audio in processed_waves:
                segments = self._split_audio(processed_audio)
                for segment in segments:
                    if len(segment) >= 16000:
                        test_embedding = self.embedding_extractor.extract_embedding(segment)
                        segment_scores = {}
                        
                        for speaker, profile_embeddings in self.speaker_profiles.items():
                            similarities = F.cosine_similarity(
                                test_embedding.unsqueeze(1),
                                profile_embeddings.unsqueeze(0),
                                dim=2
                            )
                            score = self._calculate_score(similarities, speaker, test_embedding)
                            segment_scores[speaker] = score
                        
                        if segment_scores:
                            best_speaker = max(segment_scores.items(), key=lambda x: x[1])
                            segment_predictions.append(best_speaker)
            
            if not segment_predictions:
                return "Unknown Speaker", 0.0
            
            # 투표 기반 화자 식별
            speaker_votes = defaultdict(list)
            for speaker, score in segment_predictions:
                speaker_votes[speaker].append(score)
            
            # 최종 화자 선정 (투표 수와 평균 신뢰도 고려)
            final_scores = {
                speaker: (len(scores), np.mean(scores))
                for speaker, scores in speaker_votes.items()
            }
            
            best_speaker = max(
                final_scores.items(),
                key=lambda x: (x[1][0], x[1][1])  # 투표 수 우선, 동률시 신뢰도
            )
            
            speaker_name = best_speaker[0]
            confidence = best_speaker[1][1]
            
            # 임계값 기반 최종 판단
            if confidence > self.similarity_threshold:
                return speaker_name, confidence
            else:
                return "Unknown Speaker", confidence
            
        except Exception as e:
            print(f"Error during speaker identification: {e}")
            return None, None
        finally:
            if audio_path.endswith('.wav') and 'tmp' in audio_path:
                os.remove(audio_path)

def convert_to_wav(file_path):
    """오디오 파일을 WAV 형식으로 변환"""
    temp_wav = tempfile.NamedTemporaryFile(delete=False, suffix=".wav").name
    command = [
        "ffmpeg", "-i", file_path,
        "-acodec", "pcm_s16le",
        "-ar", "16000",
        "-ac", "1",
        "-af", "silenceremove=stop_periods=-1:stop_duration=1:stop_threshold=-50dB",
        temp_wav, "-y"
    ]
    try:
        subprocess.run(command, check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        return temp_wav
    except subprocess.CalledProcessError as e:
        print(f"Error converting {file_path} to .wav: {e}")
        return None

def main():
    """메인 실행 함수"""
    # 시스템 초기화
    speaker_system = SpeakerRecognitionSystem()
    
    # 데이터 폴더 설정
    data_folder = './audio_data'
    test_folder = './test'
    
    # 화자 등록
    print("\nRegistering speakers...")
    for person_name in os.listdir(data_folder):
        person_path = os.path.join(data_folder, person_name)
        if os.path.isdir(person_path):
            speaker_system.add_speaker(person_name, person_path)
    
    # 테스트
    print("\nTesting speaker recognition:")
    results = []
    for test_file in os.listdir(test_folder):
        if test_file.endswith(('.wav', '.m4a')):
            test_path = os.path.join(test_folder, test_file)
            identified_speaker, confidence = speaker_system.identify_speaker(test_path)
            
            result = {
                'file': test_file,
                'speaker': identified_speaker,
                'confidence': confidence
            }
            results.append(result)
            
            print(f"\nTest file: {test_file}")
            print(f"Identified speaker: {identified_speaker}")
            print(f"Confidence: {confidence:.4f}")
    
    # 결과 분석
    print("\nResults Summary:")
    speakers_detected = defaultdict(int)
    avg_confidence = defaultdict(list)
    
    for result in results:
        speakers_detected[result['speaker']] += 1
        avg_confidence[result['speaker']].append(result['confidence'])
    
    print("\nDetection Statistics:")
    for speaker, count in speakers_detected.items():
        avg_conf = np.mean(avg_confidence[speaker])
        print(f"{speaker}: {count} detections, Average confidence: {avg_conf:.4f}")

if __name__ == "__main__":
    main()