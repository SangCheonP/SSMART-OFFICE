import React, { useEffect, useRef } from "react";
import styles from "@/styles/Message/Chat.module.css";
import ChatBalloon from "./ChatBalloon";
import MessageBox from "./MessageBox";
import useMessageStore from "@/store/useMessageStore";
import useAttendanceStore from "@/store/useAttendanceStore";
import useMyInfoStore from "@/store/useMyInfoStore";

const Chat = ({ selectedMember }) => {
  const chatMessagesRef = useRef();
  const { messages, createAndSubscribeToChatRoom, sendMessage, addMessage } =
    useMessageStore();

  // 현재 사용자 userId 가져오기
  const { userId: userId, profileImageUrl: currentProfileImageUrl } =
    useMyInfoStore();
  const { fetchUserSeats, memberSeats } = useAttendanceStore();

  useEffect(() => {
    if (selectedMember) {
      createAndSubscribeToChatRoom(selectedMember.userId);
      fetchUserSeats(selectedMember.userId);
    }
  }, [selectedMember, createAndSubscribeToChatRoom, fetchUserSeats]);

  // 메시지 전송 처리
  const handleSendMessage = (messageContent) => {
    const message = {
      content: messageContent,
      createdAt: new Date().toISOString(),
      userId: userId,
    };
    // addMessage(message);
    sendMessage(messageContent);
  };

  useEffect(() => {
    if (chatMessagesRef.current) {
      chatMessagesRef.current.scrollTop = chatMessagesRef.current.scrollHeight;
    }
  }, [messages]);

  const seatInfo =
    memberSeats[selectedMember?.userId]?.info || "좌석 정보 없음";

  return (
    <div className={styles.chat_container}>
      <div className={styles.chat_box}>
        {selectedMember && (
          <div className={styles.member_card}>
            <img
              src={selectedMember.profileImageUrl || "/default-profile.png"}
              alt={`${selectedMember.name || "알 수 없는 사용자"}'s profile`}
              className={styles.profile_image}
              onError={(e) => (e.target.src = "/default-profile.png")}
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
              <div className={styles.location}>{seatInfo}</div>
            </div>
          </div>
        )}
        <div className={styles.chat_messages} ref={chatMessagesRef}>
          {messages
            .slice()
            .sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
            .map((message, index) => (
              <ChatBalloon
                key={index}
                message={message.content}
                createdTime={message.createdAt || Date.now()}
                isSender={message.userId === userId}
                profileImageUrl={
                  message.userId === selectedMember.userId
                    ? selectedMember.profileImageUrl || "/default-user.png"
                    : currentProfileImageUrl || "/default-profile.png"
                }
              />
            ))}
          <div></div>
          <MessageBox onSendMessage={handleSendMessage} />
        </div>
      </div>
    </div>
  );
};

export default Chat;
