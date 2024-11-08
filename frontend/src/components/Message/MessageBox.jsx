import React, { useRef } from "react";
import styles from "@/styles/Message/Chat.module.css";
import File from "@/assets/Message/AddFile.svg?react";

const MessageBox = ({ placeholder = "메시지를 입력하세요" }) => {
  const textareaRef = useRef(null);

  const handleInput = () => {
    const textarea = textareaRef.current;
    textarea.style.height = "auto"; // 초기 높이 설정
    textarea.style.height = `${Math.min(textarea.scrollHeight, 60)}px`; // 최대 3줄 높이까지 확장
  };
  return (
    <div className={styles.message_box}>
      <textarea
        className={styles.message_input}
        placeholder={placeholder}
        rows="1"
        onInput={handleInput}
      />
      <File className={styles.file_icon} />
      <button className={styles.send_button}>전송</button>
    </div>
  );
};

export default MessageBox;
