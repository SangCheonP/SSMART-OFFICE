import React, { useState } from "react";
import MemberList from "../components/Message/MemberList";
import styles from "@/styles/Message/Message.module.css";
import AddMember from "./../assets/Message/AddMember.svg?react";
import Chat from "../components/Message/Chat";
import SearchBar from "../components/common/SearchBar";

const Message = () => {
  const [selectedMember, setSelectedMember] = useState(null);
  // 임시 데이터
  const memberData = [
    {
      userId: 1,
      employeeNumber: "S000001",
      email: "gildong@gmail.com",
      name: "김진기",
      position: "사장",
      duty: "AI & 하드웨어",
      profileImageUrl: "http://s3.gildong.png",
      status: "ACTIVE",
      createdAt: "2024-11-03T16:36:24.768188800",
    },
    {
      userId: 2,
      employeeNumber: "S000002",
      email: "gildong@gmail.com",
      name: "박상천",
      position: "이사",
      duty: "인프라",
      profileImageUrl: "http://s3.gildong.png",
      status: "OFF_DUTY",
      createdAt: "2024-11-03T16:36:24.768188800",
    },
    {
      userId: 3,
      employeeNumber: "S000003",
      email: "gildong@gmail.com",
      name: "이소연",
      position: "사원",
      duty: "프론트엔드 & 디자인",
      profileImageUrl: "http://s3.gildong.png",
      status: "INACTIVE",
      createdAt: "2024-11-03T16:36:24.768188800",
    },
  ];
  const handleMemberSelect = (name) => {
    const selected = memberData.find((member) => member.name === name);
    setSelectedMember(selected);
  };
  return (
    <div className={styles.message_container}>
      <div className={styles.member_box}>
        <SearchBar />
        {/* <AddMember className={styles.add_member} /> */}
        {/* 검색창 */}
        <MemberList
          memberData={memberData}
          onMemberSelect={handleMemberSelect}
        />
      </div>
      <Chat selectedMember={selectedMember} className={styles.chat_container} />
    </div>
  );
};

export default Message;
