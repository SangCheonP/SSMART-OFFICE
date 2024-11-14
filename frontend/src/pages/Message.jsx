import React, { useEffect, useState } from "react";
import MemberList from "@/components/Message/MemberList";
import styles from "@/styles/Message/Message.module.css";
import Chat from "@/components/Message/Chat";
import SearchBar from "@/components/common/SearchBar";
import useAttendanceStore from "@/store/useAttendanceStore";

function Message() {
  const [selectedMember, setSelectedMember] = useState(null);
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
      <div className={styles.member_box}>
        <SearchBar />

        <MemberList
          memberData={memberData}
          onMemberSelect={handleMemberSelect}
        />
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
