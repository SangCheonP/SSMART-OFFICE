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
      createAndSubscribeToChatRoom(selectedMember.userId); // userId 전달
    }
  }, [selectedMember, createAndSubscribeToChatRoom]);

  // 메시지 전송 처리
  const handleSendMessage = (messageContent) => {
    const message = {
      content: messageContent,
      createdAt: new Date().toISOString(),
      userId: currentUserId, // 현재 사용자 ID
    };

    // 상태에 메시지 추가 (렌더링되지 않았을 때도 오른쪽에 위치)
    addMessage(message);

    // 서버로 메시지 전송
    sendMessage(messageContent);
  };

  return (
    <div className={styles.chat_container}>
      <div className={styles.chat_box}>
        {selectedMember && (
          <div className={styles.member_card}>
            <img
              src={selectedMember.profileImageUrl || "/default-profile.png"} // 선택된 멤버의 프로필 URL
              alt={`${selectedMember.name || "알 수 없는 사용자"}'s profile`}
              className={styles.profile_image}
              onError={(e) => (e.target.src = "/default-profile.png")} // 이미지 로드 실패 시 기본 이미지 표시
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
        {/* 채팅 메시지 영역 */}
        <div className={styles.chat_messages}>
          {messages
            .slice()
            .sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
            .map((message, index) => (
              <ChatBalloon
                key={index}
                message={message.content}
                createdTime={message.createdAt || Date.now()}
                isSender={message.userId === currentUserId} // userId 비교
                profileImageUrl={
                  message.userId === currentUserId
                    ? currentProfileImageUrl || "/default-profile.png" // 현재 사용자 프로필
                    : selectedMember.profileImageUrl || "/default-user.png" // 선택된 멤버 프로필
                }
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
