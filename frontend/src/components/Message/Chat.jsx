import React, { useEffect } from "react";
import styles from "@/styles/Message/Chat.module.css";
import ChatBalloon from "./ChatBalloon";
import MessageBox from "./MessageBox";
import useMessageStore from "@/store/useMessageStore";
import useAttendanceStore from "@/store/useAttendanceStore";
import useMyInfoStore from "@/store/useMyInfoStore";

const Chat = ({ selectedMember }) => {
  const { messages, createAndSubscribeToChatRoom, sendMessage, addMessage } =
    useMessageStore();
  const { memberData } = useAttendanceStore();

  // 현재 사용자 정보 가져오기
  const {
    employeeNumber: currentEmployeeNumber,
    profileImageUrl: currentProfileImageUrl,
  } = useMyInfoStore();

  // 현재 사용자 userId 가져오기
  const currentUserId = memberData.find(
    (member) => member.employeeNumber === currentEmployeeNumber
  )?.userId;

  useEffect(() => {
    if (selectedMember) {
      createAndSubscribeToChatRoom(selectedMember.userId);
    }
  }, [selectedMember, createAndSubscribeToChatRoom]);

  // 메시지 전송 처리
  const handleSendMessage = (messageContent) => {
    const message = {
      content: messageContent,
      createdAt: new Date().toISOString(),
      userId: currentUserId,
    };

    addMessage(message);
    sendMessage(messageContent);
  };

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
              <div className={styles.location}>고쳐라1층 B102 06 좌석</div>
            </div>
          </div>
        )}
        <div className={styles.chat_messages}>
          {messages
            .slice()
            .sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
            .map((message, index) => (
              <ChatBalloon
                key={index}
                message={message.content}
                createdTime={message.createdAt || Date.now()}
                isSender={message.userId === currentUserId}
                profileImageUrl={
                  message.userId === currentUserId
                    ? currentProfileImageUrl || "/default-profile.png"
                    : selectedMember.profileImageUrl || "/default-user.png"
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
