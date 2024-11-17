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
  const { memberData, searchResults, fetchUserList, fetchFindUser } =
    useAttendanceStore();

  useEffect(() => {
    fetchUserList();
  }, [fetchUserList]);

  // 검색한다면 검색 결과 보여주기
  const displayedMembers =
    searchResults.length > 0 ? searchResults : memberData;

  const handleMemberSelect = (member) => {
    setSelectedMember(member);
  };

  // 검색어 처리
  const handleSearch = async (query) => {
    if (query.trim() === "") {
      await fetchUserList(); // 검색어가 비어 있으면 기본 데이터 로드
    } else {
      await fetchFindUser(query); // 검색어가 있으면 검색 API 호출
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
          {/* 검색 바에 handleSearch 전달 */}
          <SearchBar onSearch={handleSearch} />
          {view === "recentMessages" && (
            <RecentChatList onMemberSelect={handleMemberSelect} />
          )}
          {view === "memberList" && (
            <MemberList
              memberData={displayedMembers}
              onMemberSelect={handleMemberSelect}
            />
          )}
        </div>
      </div>

      {/* 선택된 사원이 있을 경우 채팅 창 표시 */}
      {selectedMember && <Chat selectedMember={selectedMember} />}
    </div>
  );
}

export default Message;
