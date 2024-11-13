import React, { useEffect, useState } from "react";
import MemberList from "@/components/Message/MemberList";
import styles from "@/styles/Attendance/Member.module.css";
import AddMember from "@/assets/Message/AddMember.svg?react";
import SearchBar from "@/components/common/SearchBar";
import AttendanceCalendar from "@/components/Attendance/AttendanceCalendar";
import useAttendanceStore from "@/store/useAttendanceStore"; // store에서 불러오는 거..
import { fetchUserAttendance } from "@/services/attendance"; // 출퇴근 정보 조회는 store에 저장할 필요 없을 것 같아서 빼둠

const Attendance = () => {
  const [selectedMember, setSelectedMember] = useState(null);
  const [attendanceData, setAttendanceData] = useState([]);
  const [userTodoData, setUserTodoData] = useState([]);
  const { memberData, fetchUserList, fetchUserTodo } = useAttendanceStore();

  // 전체 사용자 목록
  useEffect(() => {
    fetchUserList();
  }, [fetchUserList]);

  const handleMemberSelect = async (name) => {
    const selected = memberData.find((member) => member.name === name);
    setSelectedMember(selected);
    if (selected) {
      const todos = await fetchUserTodo(selected.userId);
      setUserTodoData(todos.data.data.assignments); // 일정 데이터 저장
      const month =
        new Date().getFullYear() * 100 + (new Date().getMonth() + 1);
      const day = new Date().getDate();
      handleFetchAttendance(selected.userId, month, day); // 현재 날짜로 출퇴근 정보 조회
    }
  };

  // 출퇴근 정보 가져오기
  const handleFetchAttendance = async (userId, month, day) => {
    try {
      const response = await fetchUserAttendance(userId, month, day);
      setAttendanceData(response.data.data); // 출퇴근 정보 상태에 저장
    } catch (error) {
      console.error("출퇴근 정보 가져오는 중 오류 발생:", error);
    }
  };

  return (
    <div className={styles.member_container}>
      <div className={styles.member_box}>
        <div className={styles.search_box}>
          <SearchBar className={styles.search_bar} />
          <AddMember className={styles.add_member} />
        </div>
        <MemberList
          memberData={memberData}
          onMemberSelect={handleMemberSelect}
        />
      </div>
      {selectedMember && (
        <div className={styles.todo_container}>
          <div className={styles.calendar_box}>
            <AttendanceCalendar
              userId={selectedMember.userId}
              attendanceData={attendanceData}
              userTodoData={userTodoData}
              onDateSelect={(date) => {
                const month = date.getFullYear() * 100 + (date.getMonth() + 1);
                const day = date.getDate();
                handleFetchAttendance(selectedMember.userId, month, day);
              }}
            />
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
