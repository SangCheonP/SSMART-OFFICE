import React, { useEffect } from "react";
import styles from "@/styles/Message/Chat.module.css";
import ChatBalloon from "./ChatBalloon";
import MessageBox from "./MessageBox";
import useMessageStore from "@/store/useMessageStore";

const Chat = ({ selectedMember }) => {
  const { messages, createAndSubscribeToChatRoom, sendMessage } =
    useMessageStore();

  useEffect(() => {
    console.log("selectedMember:", selectedMember);
    if (selectedMember) {
      createAndSubscribeToChatRoom(selectedMember);
    } else {
      console.warn("selectedMember가 없습니다.");
    }
  }, [selectedMember, createAndSubscribeToChatRoom]);

  const handleSendMessage = (messageContent) => {
    sendMessage(messageContent);
  };

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
          {messages.map((message, index) => (
            <ChatBalloon
              key={index}
              message={message.content}
              createdTime={message.createdTime || Date.now()}
              isSender={message.isSender || true}
              profileImageUrl={message.profileImageUrl || "default-profile.png"}
            />
          ))}
          {/* 입력창 */}
          <MessageBox onSendMessage={handleSendMessage} />
        </div>
      </div>
    </div>
  );
};

export default Chat;
