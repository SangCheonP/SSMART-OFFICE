import React, { useEffect, useState } from "react";
import MemberList from "@/components/Attendance/AttendanceMemberList";
import styles from "@/styles/Attendance/Member.module.css";
import AddMember from "@/assets/Message/AddMember.svg?react";
import SearchBar from "@/components/common/SearchBar";
import AttendanceCalendar from "@/components/Attendance/AttendanceCalendar";
import useAttendanceStore from "@/store/useAttendanceStore";
import { fetchUserAttendance, fetchUserList } from "@/services/attendanceApi";
import AddMemberModal from "@/components/Modals/AddMemberModal";
import useModalStore from "@/store/useModalStore";
import { isSameDay } from "date-fns";
import TodoList from "@/components/Attendance/AttendanceTodoList";

const Attendance = () => {
  const openModal = useModalStore((state) => state.openModal);
  const [selectedMember, setSelectedMember] = useState(null);
  const [attendanceData, setAttendanceData] = useState([]);
  const [selectedAttendance, setSelectedAttendance] = useState([]);
  const [userTodoData, setUserTodoData] = useState([]);
  const { memberData, fetchUserList, fetchUserTodo } = useAttendanceStore();

  // 전체 사용자 목록
  useEffect(() => {
    fetchUserList();
  }, [fetchUserList]);

  // 오늘 날짜 출퇴근 정보 기본 설정
  const getToday = () => new Date();
  useEffect(() => {
    if (selectedMember && selectedMember.userId) {
      const today = getToday();
      const month = today.getFullYear() * 100 + (today.getMonth() + 1);
      const day = today.getDate();
      handleFetchAttendance(selectedMember.userId, month, day, today);
    }
  }, [selectedMember]);

  // 사원 선택
  const handleMemberSelect = async (name) => {
    const selected = memberData.find((member) => member.name === name);
    setSelectedMember(selected);

    if (selected && selected.userId) {
      try {
        const month =
          new Date().getFullYear() * 100 + (new Date().getMonth() + 1);
        const day = new Date().getDate();
        const todos = await fetchUserTodo(selected.userId, month, day);

        if (todos && todos.data) {
          setUserTodoData(todos.data);
        } else {
          console.warn("일정이 없습니다.");
          setUserTodoData([]);
        }
      } catch (error) {
        console.error("사원 일정 데이터를 가져오는 중 오류 발생:", error);
      }
    } else {
      console.warn("유효하지 않은 사용자 ID:", selected?.userId);
    }
  };

  // 출퇴근 정보 가져오기
  const handleFetchAttendance = async (userId, month, day, selectedDate) => {
    try {
      const response = await fetchUserAttendance(userId, month, day);
      setAttendanceData(response.data.data);
      const filteredAttendance = response.data.data.filter((attendance) =>
        isSameDay(new Date(attendance.attendanceTime), selectedDate)
      );
      setSelectedAttendance(filteredAttendance);
    } catch (error) {
      console.error("출퇴근 정보 가져오는 중 오류 발생:", error);
    }
  };

  // 멤버 추가 버튼 클릭
  const handleAddMemberClick = () => {
    openModal(AddMemberModal, {
      onSubmit: () => {
        console.log("멤버 추가 모달입니다.");
      },
    });
  };
  // 날짜 클릭
  const handleSelectDate = async (date) => {
    const month = date.getFullYear() * 100 + (date.getMonth() + 1);
    const day = date.getDate();

    if (selectedMember?.userId) {
      await fetchUserTodo(selectedMember.userId, month, day);
      handleFetchAttendance(selectedMember.userId, month, day, date);
    }
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
            <AttendanceCalendar
              userId={selectedMember.userId}
              attendanceData={attendanceData}
              userTodoData={userTodoData}
              onDateSelect={handleSelectDate}
            />
          </div>
          <div className={styles.attendance_info}>
            <div className={styles.todo_box}>
              <div className={styles.member_name}>{selectedMember.name}</div>
              <div>님의 일과</div>
            </div>
            <ul className={styles.attendance_data}>
              {selectedAttendance.length > 0 ? (
                selectedAttendance.map((attendance, index) => (
                  <li key={index}>
                    {new Date(attendance.attendanceTime).toLocaleTimeString()}
                  </li>
                ))
              ) : (
                <li>출퇴근 정보가 없습니다.</li>
              )}
            </ul>
          </div>
          <TodoList />
        </div>
      )}
    </div>
  );
};

export default Attendance;
