import React, { useState } from "react";
import styles from "@/styles/Message/Chat.module.css";
import MessageBox from "./MessageBox";
import ChatBalloon from "./ChatBalloon";

const Chat = ({ selectedMember }) => {
  // 임시 데이터
  const [messages] = useState([
    {
      messageId: "46",
      userId: 3,
      messageType: "COMMENT",
      messageContent: "뭐해",
      created_date_time: "2024-09-01T16:36:21.768188800",
    },
    {
      messageId: "56",
      userId: 3,
      messageType: "COMMENT",
      messageContent: "퇴사할거야? 오늘 할 일 다 끝내고 가",
      created_date_time: "2024-09-01T16:36:22.768188800",
    },
  ]);

  return (
    <div className={styles.chat_container}>
      <div className={styles.chat_box}>
        {selectedMember && (
          <div className={styles.member_card}>
            <img
              src={selectedMember.profileImageUrl}
              alt={`${selectedMember.name}'s profile`}
              className={styles.profile_image}
            />
            <div>
              <div className={styles.member_info}>
                <span className={styles.position}>
                  {selectedMember.position}
                </span>
                <span className={styles.name}>{selectedMember.name}</span>
                <div
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
        <div className={styles.chat_messages}>
          {messages.map((message) => (
            <ChatBalloon
              key={message.messageId}
              message={message.messageContent}
              createdTime={message.created_date_time}
              isSender={message.userId === selectedMember.userId}
              profileImageUrl={selectedMember.profileImageUrl}
            />
          ))}
          {/* 입력창 */}
          <MessageBox />
          {/* <File /> */}
        </div>
      </div>
    </div>
  );
};

export default Chat;
