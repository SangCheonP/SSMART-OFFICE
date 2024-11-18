import React, { useEffect, useState } from "react";
import MemberList from "@/components/Message/MemberList";
import RecentChatList from "../components/Message/RecentChatList";
import styles from "@/styles/Message/Message.module.css";
import Chat from "@/components/Message/Chat";
import SearchBar from "@/components/common/SearchBar";
import useAttendanceStore from "@/store/useAttendanceStore";

const Message = () => {
  const [selectedMember, setSelectedMember] = useState(null);
  const [selectedChatRoomId, setSelectedChatRoomId] = useState(null);
  const [view, setView] = useState("memberList");
  const { memberData, searchResults, fetchUserList, fetchFindUser } =
    useAttendanceStore();

  useEffect(() => {
    fetchUserList();
  }, [fetchUserList]);

  // 검색한다면 검색 결과 보여주기
  const displayedMembers =
    searchResults.length > 0 ? searchResults : memberData;

  const handleMemberSelect = (userId) => {
    const selected = memberData.find((member) => member.userId === userId);
    setSelectedMember(selected);
  };
  const handleChatRoomSelect = (chatRoomId, chatRoomMemberId) => {
    setSelectedChatRoomId(chatRoomId);
    const memberInfo = memberData.find(
      (member) => member.userId === chatRoomMemberId
    );
    setSelectedMember(memberInfo || null);
  };

  // 검색어 처리
  const handleSearch = async (query) => {
    const { clearSearchResults } = useAttendanceStore.getState();
    if (query.trim() === "") {
      clearSearchResults();
      await fetchUserList();
    } else {
      await fetchFindUser(query);
    }
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
          <SearchBar onSearch={handleSearch} />
          {view === "recentMessages" && (
            <RecentChatList onChatRoomSelect={handleChatRoomSelect} />
          )}
          {view === "memberList" && (
            <MemberList
              memberData={displayedMembers}
              onMemberSelect={handleMemberSelect}
            />
          )}
        </div>
      </div>

      {selectedMember && <Chat selectedMember={selectedMember} />}
    </div>
  );
};

export default Message;
