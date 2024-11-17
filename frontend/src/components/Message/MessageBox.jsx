import React, { useRef, useCallback } from "react";
import styles from "@/styles/Message/Chat.module.css";
import File from "@/assets/Message/AddFile.svg?react";

const MessageBox = ({ placeholder = "메시지를 입력하세요", onSendMessage }) => {
  const textareaRef = useRef(null);
  const inputRef = useRef(null);
  const [inputMessage, setInputMessage] = React.useState("");
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

  const onUploadImage = useCallback(async (event) => {
    const file = event.target.files[0];
    const MAX_FILE_SIZE = 5 * 1024 * 1024;
    if (file) {
      if (file.size > MAX_FILE_SIZE) {
        alert("파일 크기는 5MB를 초과할 수 없습니다.");
        return;
      }
      if (!SUPPORTED_FORMATS.includes(file.type)) {
        alert("지원하지 않는 파일 형식입니다.");
        return;
      }

      const formData = new FormData();
      formData.append("file", file);

      // TODO api url로 바꾸기
      try {
        const response = await fetch(
          "http://localhost:8080/api/v1/upload-file",
          {
            method: "POST",
            body: formData,
          }
        );
        const result = await response.json();
        if (result.status === 200) {
          console.log("파일 업로드 성공:", result.data.fileUrl);
        } else {
          console.error("파일 업로드 실패:", result.msg);
          alert("파일 업로드에 실패했습니다.");
        }
      } catch (error) {
        console.error("파일 업로드 에러:", error);
        alert("파일 업로드 중 오류가 발생했습니다.");
      }
    }
  }, []);

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
      />
      <File className={styles.file_icon} onClick={onFileIconClick} />
      <input
        type="file"
        accept="image/*"
        ref={inputRef}
        style={{ display: "none" }}
        onChange={onUploadImage}
      />
      <button className={styles.send_button} onClick={handleSendMessage}>
        전송
      </button>
    </div>
  );
};

export default MessageBox;
