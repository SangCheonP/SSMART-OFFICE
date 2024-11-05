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
        
        waveform = torch.from_numpy(
            librosa.effects.time_stretch(waveform.numpy(), rate=speed_factor)
        ).float()
        
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
        noise_levels = [0.001, 0.003, 0.005]
        for level in noise_levels:
            augmented_waves.append(self.add_noise(waveform, level))
        
        speed_factors = [0.9, 1.1]
        for factor in speed_factors:
            augmented_waves.append(self.change_speed(waveform, factor))
        
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
        if len(waveform.shape) > 1 and waveform.shape[0] > 1:
            waveform = torch.mean(waveform, dim=0, keepdim=True)
        
        if sample_rate != self.target_sr:
            resampler = torchaudio.transforms.Resample(orig_freq=sample_rate, new_freq=self.target_sr)
            waveform = resampler(waveform)
        
        waveform = self.remove_silence(waveform.squeeze())
        
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
            inputs = self.feature_extractor(
                waveform.numpy(), 
                sampling_rate=16000,
                padding=True,
                return_tensors="pt"
            )
            inputs = {k: v.to(device) for k, v in inputs.items()}
            
            outputs = self.model(**inputs)
            hidden_states = outputs.last_hidden_state
            
            mean_embedding = hidden_states.mean(dim=1)
            std_embedding = hidden_states.std(dim=1)
            max_embedding = hidden_states.max(dim=1).values
            
            pitch_features = self.preprocessor.feature_extractor.extract_pitch(waveform)
            mfcc_features = self.preprocessor.feature_extractor.extract_mfcc(waveform)
            spectral_features = self.preprocessor.feature_extractor.extract_spectral_features(waveform)
            
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
        print(f"\n화자를 등록 중입니다: {speaker_name}")
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
                            if len(segment) >= 16000:
                                embedding = self.embedding_extractor.extract_embedding(segment)
                                embeddings.append(embedding)
                                embedding_stats.append(embedding.cpu().numpy())
                    
                    print(f"성공적으로 처리된 파일: {file_name}")
                    
                except Exception as e:
                    print(f"파일 처리 중 오류 발생: {file_name}, 오류 내용: {e}")
                finally:
                    if file_path.endswith('.wav') and 'tmp' in file_path:
                        os.remove(file_path)
        
        if embeddings:
            self.speaker_profiles[speaker_name] = torch.cat(embeddings, dim=0)
            
            embedding_stats = np.vstack(embedding_stats)
            self.speaker_stats[speaker_name] = {
                '평균': np.mean(embedding_stats, axis=0),
                '표준편차': np.std(embedding_stats, axis=0),
                '범위': (np.min(embedding_stats, axis=0), np.max(embedding_stats, axis=0))
            }
            
            print(f"화자가 성공적으로 등록되었습니다: {speaker_name}")
        else:
            print(f"화자 등록 실패: {speaker_name}")
    
    def _split_audio(self, waveform, segment_length=32000):
        segments = []
        for i in range(0, len(waveform), segment_length):
            segment = waveform[i:i + segment_length]
            if len(segment) >= 16000:
                segments.append(segment)
        return segments
    
    def _calculate_score(self, similarities, speaker_name, test_embedding):
        max_similarity = similarities.max().item()
        
        test_embedding_np = test_embedding.cpu().numpy()
        stats = self.speaker_stats[speaker_name]
        
        z_score = np.abs((test_embedding_np - stats['평균']) / (stats['표준편차'] + 1e-6))
        stat_score = 1 - np.mean(z_score)
        
        range_min, range_max = stats['범위']
        in_range = np.logical_and(
            test_embedding_np >= range_min,
            test_embedding_np <= range_max
        )
        # range_score = np.mean(in_range)
        range_score = np.clip(np.mean(in_range), 0, 1)
        
        final_score = (0.5 * max_similarity) + (0.3 * stat_score) + (0.2 * range_score)
        return final_score
    
    def identify_speaker(self, audio_path):
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
                return "알 수 없는 화자", 0.0
            
            speaker_votes = defaultdict(list)
            for speaker, score in segment_predictions:
                speaker_votes[speaker].append(score)
            
            final_scores = {
                speaker: (len(scores), np.mean(scores))
                for speaker, scores in speaker_votes.items()
            }
            
            best_speaker = max(
                final_scores.items(),
                key=lambda x: (x[1][0], x[1][1])
            )
            
            speaker_name = best_speaker[0]
            confidence = best_speaker[1][1]
            
            if confidence > self.similarity_threshold:
                return speaker_name, confidence
            else:
                return "알 수 없는 화자", confidence
            
        except Exception as e:
            print(f"화자 식별 중 오류 발생: {e}")
            return None, None
        finally:
            if audio_path.endswith('.wav') and 'tmp' in audio_path:
                os.remove(audio_path)

def convert_to_wav(file_path):
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
        print(f"{file_path}를 .wav로 변환하는 중 오류 발생: {e}")
        return None

def main():
    speaker_system = SpeakerRecognitionSystem()
    
    data_folder = './audio_data'
    test_folder = './test'
    
    print("\n화자 등록 중...")
    for person_name in os.listdir(data_folder):
        person_path = os.path.join(data_folder, person_name)
        if os.path.isdir(person_path):
            speaker_system.add_speaker(person_name, person_path)
    
    print("\n화자 인식 테스트:")
    results = []
    for test_file in os.listdir(test_folder):
        if test_file.endswith(('.wav', '.m4a')):
            test_path = os.path.join(test_folder, test_file)
            identified_speaker, confidence = speaker_system.identify_speaker(test_path)
            
            result = {
                '파일': test_file,
                '화자': identified_speaker,
                '신뢰도': confidence
            }
            results.append(result)
            
            print(f"\n테스트 파일: {test_file}")
            print(f"인식된 화자: {identified_speaker}")
            print(f"신뢰도: {confidence:.4f}")
    
    print("\n결과 요약:")
    speakers_detected = defaultdict(int)
    avg_confidence = defaultdict(list)
    
    for result in results:
        speakers_detected[result['화자']] += 1
        avg_confidence[result['화자']].append(result['신뢰도'])
    
    print("\n인식 통계:")
    for speaker, count in speakers_detected.items():
        avg_conf = np.mean(avg_confidence[speaker])
        print(f"{speaker}: {count}회 인식, 평균 신뢰도: {avg_conf:.4f}")

if __name__ == "__main__":
    main()
