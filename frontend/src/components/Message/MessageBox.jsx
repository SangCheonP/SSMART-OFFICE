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
      console.log("전송할 메시지 내용:", inputMessage); // 메시지 내용 출력
      onSendMessage(inputMessage); // 메시지 전송
      setInputMessage(""); // 입력 초기화
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
        // Shift + Enter: 줄바꿈
        return;
      } else {
        // Enter: 메시지 전송
        event.preventDefault(); // 줄바꿈 방지
        handleSendMessage();
      }
    }
  };
  const onUploadImage = useCallback(
    async (event) => {
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
        const response = await updateImageFile(file);
        const fileUrl = response?.data?.fileUrl;
        if (fileUrl) {
          // 파일 업로드 성공 시 메시지 전송
          const messageWithFile = `[파일 업로드] ${fileUrl}`;
          onSendMessage(messageWithFile);
          console.log("파일 업로드 성공:", fileUrl);
        } else {
          alert("파일 업로드에 실패했습니다.");
        }
      } catch (error) {
        console.error("파일 업로드 에러:", error);
        alert("파일 업로드 중 오류가 발생했습니다.");
      } finally {
        setIsUploading(false);
      }
    },
    [onSendMessage]
  );

  const onFileIconClick = () => {
    inputRef.current.click();
  };

  return (
    <div className={styles.message_box}>
      <textarea
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
        {isUploading ? "업로드 중..." : "전송"}
      </button>
    </div>
  );
};

export default MessageBox;
