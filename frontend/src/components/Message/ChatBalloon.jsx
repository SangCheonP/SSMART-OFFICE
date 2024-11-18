import React from "react";
import styles from "@/styles/Message/ChatBalloon.module.css";

const ChatBalloon = ({ message, createdTime, isSender, profileImageUrl }) => {
  const formatTime = (time) => {
    const date = new Date(time);
    return `${date.getHours()}:${String(date.getMinutes()).padStart(2, "0")}`;
  };

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
            <div className={styles.message_content}>{message}</div>
          </div>
          <span className={styles.created_time}>{formatTime(createdTime)}</span>
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
