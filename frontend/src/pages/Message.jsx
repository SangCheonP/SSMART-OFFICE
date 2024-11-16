import React, { useEffect, useState } from "react";
import MemberList from "@/components/Message/MemberList";
import RecentChatList from "../components/Message/RecentChatList";
import styles from "@/styles/Message/Message.module.css";
import Chat from "@/components/Message/Chat";
import SearchBar from "@/components/common/SearchBar";
import useAttendanceStore from "@/store/useAttendanceStore";

function Message() {
  const [selectedMember, setSelectedMember] = useState(null);
  const [view, setView] = useState("memberList");
  const { memberData, fetchUserList } = useAttendanceStore();

  useEffect(() => {
    fetchUserList();
  }, [fetchUserList]);

  const handleMemberSelect = (name) => {
    const selected = memberData.find((member) => member.name === name);
    setSelectedMember(selected);
  };
  return (
    <div className={styles.message_container}>
      <div className={styles.list_box}>
        <div className={styles.member_box}>
          <div className={styles.button_list}>
            <button
              className={
                view === "memberList"
                  ? styles.active_button
                  : styles.inactive_button
              }
              onClick={() => setView("memberList")}
            >
              사원 목록
            </button>
            <button
              className={
                view === "recentMessages"
                  ? styles.active_button
                  : styles.inactive_button
              }
              onClick={() => setView("recentMessages")}
            >
              최신 메시지
            </button>
          </div>
          <SearchBar />
          {view === "recentMessages" && (
            <RecentChatList onMemberSelect={handleMemberSelect} />
          )}
          {view === "memberList" && (
            <MemberList
              memberData={memberData}
              onMemberSelect={handleMemberSelect}
            />
          )}
        </div>
      </div>

      {selectedMember && (
        <Chat
          selectedMember={selectedMember}
          className={styles.chat_container}
        />
      )}
    </div>
  );
}

export default Message;
