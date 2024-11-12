import React, { useState } from "react";
import MemberList from "@/components/Message/MemberList";
import styles from "@/styles/Attendance/Member.module.css";
import AddMember from "@/assets/Message/AddMember.svg?react";
import SearchBar from "@/components/common/SearchBar";
import AttendanceCalendar from "@/components/Attendance/AttendanceCalendar";
import AddMemberModal from "@/components/Modals/AddMemberModal";

import useModalStore from "@/store/useModalStore";

const Attendance = () => {
  const openModal = useModalStore((state) => state.openModal);
  const [selectedMember, setSelectedMember] = useState(null);
  const [selectedDate, setSelectedDate] = useState(new Date());
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

  const handleAddMemberClick = () => {
    openModal(AddMemberModal, {
      onSubmit: () => {
        console.log("비밀번호 수정 모달입니다.");
      },
    });
  };
  // 선택된 날짜 변경 핸들러 함수
  const handleDateChange = (date) => {
    setSelectedDate(date);
    // 선택된 날짜에 맞는 작업 수행
  };
  return (
    <div className={styles.member_container}>
      <div className={styles.member_box}>
        <div className={styles.search_box}>
          <SearchBar className={styles.search_bar} />
          <AddMember
            className={styles.add_member}
            onClick={handleAddMemberClick}
          />
        </div>
        <MemberList
          memberData={memberData}
          onMemberSelect={handleMemberSelect}
        />
      </div>
      {selectedMember && (
        <div className={styles.todo_container}>
          <div className={styles.calendar_box}>
            <AttendanceCalendar />
          </div>
          <div className={styles.todo_box}>
            <div className={styles.member_name}>{selectedMember.name}</div>
            <div>님의 일과</div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Attendance;
