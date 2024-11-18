import React, { useRef, useCallback } from "react";
import styles from "@/styles/Message/Chat.module.css";
import File from "@/assets/Message/AddFile.svg?react";
import { updateImageFile } from "@/services/fileAPI";

const MessageBox = ({ placeholder = "메시지를 입력하세요", onSendMessage }) => {
  const textareaRef = useRef(null);
  const inputRef = useRef(null);
  const [inputMessage, setInputMessage] = React.useState("");
  const [isUploading, setIsUploading] = React.useState(false);
  const SUPPORTED_FORMATS = [
    "image/jpeg",
    "image/png",
    "application/vnd.ms-powerpoint", // .ppt
    "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .pptx
    "application/msword", // .doc
    "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .docx
    "application/haansofthwp", // 한글 파일 (.hwp)
  ];

  const handleSendMessage = () => {
    if (inputMessage.trim()) {
      console.log("전송할 메시지 내용:", inputMessage);
      onSendMessage(inputMessage);
      setInputMessage("");
    }
  };
  const handleInput = () => {
    const textarea = textareaRef.current;
    textarea.style.height = "auto";
    textarea.style.height = `${Math.min(textarea.scrollHeight, 60)}px`;
  };

  const handleKeyDown = (event) => {
    if (event.key === "Enter") {
      if (event.shiftKey) {
        return;
      } else {
        event.preventDefault();
        handleSendMessage();
      }
    }
  };
  const onUploadImage = useCallback(async (event) => {
    const file = event.target.files[0];
    const MAX_FILE_SIZE = 5 * 1024 * 1024;
    if (!file) return;

    // 파일 크기 및 형식 검증
    if (file.size > MAX_FILE_SIZE) {
      alert("파일 크기는 5MB를 초과할 수 없습니다.");
      return;
    }
    if (!SUPPORTED_FORMATS.includes(file.type)) {
      alert("지원하지 않는 파일 형식입니다.");
      return;
    }

    // 업로드 시작
    setIsUploading(true);
    try {
      // 서버로 파일 업로드 및 메시지 전송 API 호출
      const response = await updateImageFile(file);

      if (response?.status === 201 && response.data?.fileUrl) {
        const fileUrl = response.data.fileUrl;
        const fileType = file.type.startsWith("image/") ? "image" : "file";
        onSendMessage({ type: fileType, content: fileUrl });
      } else {
        console.error("파일 업로드 및 메시지 전송 실패:", response);
        alert("파일 업로드 및 메시지 전송에 실패했습니다.");
      }
    } catch (error) {
      console.error("파일 업로드 중 오류 발생:", error);
      alert("파일 업로드 중 오류가 발생했습니다.");
    } finally {
      setIsUploading(false);
    }
  }, []);

  const onFileIconClick = () => {
    inputRef.current.click();
  };

  return (
    <div className={styles.message_box}>
      <input
        ref={textareaRef}
        className={styles.message_input}
        placeholder={placeholder}
        value={inputMessage}
        onChange={(e) => setInputMessage(e.target.value)}
        onInput={handleInput}
        onKeyDown={handleKeyDown}
      />
      <File className={styles.file_icon} onClick={onFileIconClick} />
      <input
        type="file"
        accept="image/*,.ppt,.pptx,.doc,.docx,.hwp"
        ref={inputRef}
        style={{ display: "none" }}
        onChange={onUploadImage}
      />
      <button
        className={styles.send_button}
        onClick={handleSendMessage}
        disabled={isUploading}
      >
        {isUploading ? "업로드 중" : "전송"}
      </button>
    </div>
  );
};

export default MessageBox;
