import React, { useEffect } from "react";
import styles from "@/styles/Message/Message.module.css";
import useMessageStore from "@/store/useMessageStore";
import useAttendanceStore from "@/store/useAttendanceStore";

const RecentChatList = ({ onChatRoomSelect }) => {
  const { chatrooms, fetchAndSetChatrooms } = useMessageStore();
  const { memberData } = useAttendanceStore();

  useEffect(() => {
    fetchAndSetChatrooms(); // 채팅방 목록 가져오기
  }, [fetchAndSetChatrooms]);

  const getMemberInfo = (chatRoomMemberId) => {
    return (
      memberData.find((member) => member.userId === chatRoomMemberId) || {}
    );
  };

  const handleChatRoomClick = (chatroom) => {
    if (onChatRoomSelect) {
      onChatRoomSelect(chatroom.chatRoomId, chatroom.chatRoomMemberId);
    }
  };

  return (
    <div className={styles.member_list}>
      {chatrooms.length > 0 ? (
        chatrooms.map((chatroom) => {
          const memberInfo = getMemberInfo(chatroom.chatRoomMemberId);
          return (
            <div
              key={chatroom.chatRoomId}
              className={styles.member_card}
              onClick={() => handleChatRoomClick(chatroom)}
            >
              <img
                src={memberInfo.profileImageUrl || "default-profile.png"}
                alt={`${memberInfo.name || "알 수 없는 사용자"}'s profile`}
                className={styles.profile_image}
              />
              <div className={styles.member_info}>
                <div className={styles.member_profile}>
                  <div className={styles.member_data}>
                    <span className={styles.position}>
                      {memberInfo.position}
                    </span>
                    <span className={styles.name}>{memberInfo.name}</span>
                  </div>
                  <div>
                    <span className={styles.time}>
                      {chatroom.lastMessageTime
                        ? new Date(chatroom.lastMessageTime).toLocaleString()
                        : "시간 정보 없음"}
                    </span>
                  </div>
                </div>
                <span className={styles.last_message}>
                  {chatroom.lastMessage || "없음"}
                </span>
              </div>
            </div>
          );
        })
      ) : (
        <p className={styles.no_members}>채팅방이 없습니다.</p>
      )}
    </div>
  );
};

export default RecentChatList;
