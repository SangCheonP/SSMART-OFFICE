import React from "react";
import styles from "@/styles/Message/ChatBalloon.module.css";

const ChatBalloon = ({ message, createdTime, isSender, profileImageUrl }) => {
  const formatTime = (time) => {
    const date = new Date(time);
    return `${date.getHours()}:${String(date.getMinutes()).padStart(2, "0")}`;
  };
  const isIframeMessage = (msg) =>
    msg.startsWith("[파일 업로드] http://") ||
    msg.startsWith("[파일 업로드] https://");

  return (
    <div
      className={`${styles.balloon_container} ${
        isSender ? styles.sender : styles.receiver
      }`}
    >
      {!isSender && (
        <>
          <img
            src={profileImageUrl}
            alt="Profile"
            className={styles.profile_image}
          />
          <div className={styles.balloon_box}>
            {message.type === "image" ? (
              <img
                src={isIframeMessage}
                alt="Uploaded"
                className={styles.uploaded_image}
              />
            ) : message.type === "file" ? (
              <a
                href={message.content}
                target="_blank"
                rel="noopener noreferrer"
                className={styles.file_link}
              >
                파일 다운로드
              </a>
            ) : (
              <div className={styles.message_content}>{message.content}</div>
            )}
            <span className={styles.created_time}>
              {formatTime(createdTime)}
            </span>
          </div>
        </>
      )}
      {isSender && (
        <>
          <span className={styles.created_time}>{formatTime(createdTime)}</span>
          <div className={styles.balloon_box}>
            <div className={styles.message_content}>{message}</div>
          </div>
          <img
            src={profileImageUrl}
            alt="Profile"
            className={styles.profile_image}
          />
        </>
      )}
    </div>
  );
};

export default ChatBalloon;
