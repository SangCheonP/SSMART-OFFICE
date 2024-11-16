import { useState } from "react";
import styles from "@/styles/Message/Message.module.css";
import Modify from "@/assets/Common/Modify.svg?react";
import ModifyMemberModal from "@/components/Modals/ModifyMemberModal";
import useModalStore from "@/store/useModalStore";

const MemberList = ({ memberData, onMemberSelect }) => {
  const openModal = useModalStore((state) => state.openModal);
  const [selectedMemberId, setSelectedMemberId] = useState(null);

  const handleMemberClick = (member) => {
    setSelectedMemberId(member.userId); // 클릭된 member의 ID를 상태에 저장
    onMemberSelect(member.name); // 부모 컴포넌트로 선택된 멤버 이름 전달
  };

  const hadleModifyInfoClick = (userId) => {
    openModal(ModifyMemberModal, {
      userId: userId,
      onSubmit: () => {},
    });
  };
  return (
    <div className={styles.member_list}>
      {memberData.map((member) => (
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
          <div className={styles.member_info}>
            <div className={styles.member_profile}>
              <span className={styles.position}>{member.position}</span>
              <span className={styles.name}>{member.name}</span>
              <div
                className={`${styles.status} ${
                  member.status === "ACTIVE" ? styles.active : styles.inactive
                }`}
              ></div>
            </div>
            <div className={styles.duty}>{member.duty}</div>
            <div className={styles.location}>2층임</div>
          </div>
          <div
            className={styles.editMember}
            onClick={() => hadleModifyInfoClick(member.userId)}
          >
            <Modify />
          </div>
        </div>
      ))}
    </div>
  );
};

export default MemberList;
