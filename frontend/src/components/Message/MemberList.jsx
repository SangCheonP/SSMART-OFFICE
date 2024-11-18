import { useState, useEffect } from "react";
import styles from "@/styles/Message/Message.module.css";
import useAttendanceStore from "@/store/useAttendanceStore";

const MemberList = ({ onMemberSelect }) => {
  const [selectedMemberId, setSelectedMemberId] = useState(null);
  const { memberData, searchResults, fetchUserSeats, memberSeats } =
    useAttendanceStore();
  const [displayedMembers, setDisplayedMembers] = useState(memberData);

  // 검색 결과가 존재하면 검색 결과를, 그렇지 않으면 기본 데이터를 사용
  useEffect(() => {
    if (searchResults.length > 0) {
      setDisplayedMembers(searchResults);
      console.log(searchResults);
    } else {
      setDisplayedMembers(memberData);
      console.log(memberData);
    }
  }, [searchResults, memberData]);

  const handleMemberClick = (member) => {
    setSelectedMemberId(member.userId);
    onMemberSelect(member.userId);
    // 해당 사원의 좌석 정보를 가져오기
    fetchUserSeats(member.userId);
  };

  return (
    <div className={styles.member_list}>
      {displayedMembers.length > 0 ? (
        displayedMembers.map((member) => (
          <div
            key={member.userId}
            className={`${styles.member_card} ${
              selectedMemberId === member.userId ? styles.selected_card : ""
            }`}
            onClick={() => handleMemberClick(member)}
          >
            <img
              src={member.profileImageUrl}
              alt={`${member.name}'s profile`}
              className={styles.profile_image}
            />
            <div>
              <div className={styles.member_info}>
                <div className={styles.member_profile}>
                  <span className={styles.position}>{member.position}</span>
                  <span className={styles.name}>{member.name}</span>
                  <div
                    className={`${styles.status} ${
                      member.status === "ACTIVE"
                        ? styles.active
                        : styles.inactive
                    }`}
                  ></div>
                </div>
                <div className={styles.duty}>{member.duty}</div>
                <div className={styles.location}>
                  {memberSeats[member.userId]?.info || "좌석 없음"}
                </div>
              </div>
            </div>
          </div>
        ))
      ) : (
        <p className={styles.no_members}>사원이 없습니다.</p>
      )}
    </div>
  );
};

export default MemberList;
