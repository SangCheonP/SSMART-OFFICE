import React from "react";
import styles from "@/styles/Message/Chat.module.css";

const Chat = ({ selectedMember }) => {
  return (
    <div className={styles.chat_container}>
      <div className={styles.chat_box}>
        {selectedMember && (
          <div className={styles.member_info}>
            <img
              src={selectedMember.profileImageUrl}
              alt={`${selectedMember.name}'s profile`}
              className={styles.profile_image}
            />
            <div>
              <div>
                <span className={styles.position}>
                  {selectedMember.position}
                </span>
                <span className={styles.name}>{selectedMember.name}</span>
                <span
                  className={`${styles.status} ${
                    selectedMember.status === "ACTIVE"
                      ? styles.active
                      : styles.inactive
                  }`}
                />
              </div>
              <div className={styles.location}>1층 B102 06 좌석</div>
            </div>
          </div>
        )}
        {/* 채팅 메시지 영역 */}
        <div className={styles.chat_messages}>{/* 채팅 메시지*/}</div>
      </div>
    </div>
  );
};

export default Chat;
