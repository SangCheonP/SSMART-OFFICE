import React, { useEffect, useState } from "react";
import MyCalendar from "@/components/MyCalendar/Calendar";
import styles from "@/styles/Home/Home.module.css";
import Todo from "@/components/Todo/Todo";
import useHomeStore from "../store/useHomeStore";

const Home = () => {
  const [selectedDate, setSelectedDate] = useState(null);
  const {
    calendarData,
    todoData,
    attendanceData,
    fetchCalendarData,
    fetchTodoData,
    fetchAttendanceData,
  } = useHomeStore();

  useEffect(() => {
    const today = new Date();
    const month = "202412";
    const day = "31";

    // 캘린더 데이터 조회
    fetchCalendarData(month);
    fetchTodoData(month, day);
    fetchAttendanceData(month, day);

    console.log("Fetched Attendance Data:", attendanceData); // 디버깅 확인용
  }, [fetchCalendarData, fetchTodoData, fetchAttendanceData]);

  const handleDateSelect = (date) => {
    setSelectedDate(date);
    const month = date.getFullYear() * 100 + (date.getMonth() + 1);
    const day = date.getDate();
    fetchTodoData(month, day);
    fetchAttendanceData(month, day); // 선택한 날짜의 출퇴근 정보 조회
  };
  return (
    <div className={styles.home}>
      <div className={styles.calendar}>
        <MyCalendar
          monthData={
            Array.isArray(calendarData?.data?.attendances)
              ? calendarData.data.attendances
              : []
          }
          attendanceData={
            Array.isArray(attendanceData?.data) ? attendanceData.data : []
          }
          onDateSelect={handleDateSelect}
        />
      </div>
      <div>
        <Todo
          selectedDate={selectedDate}
          monthData={calendarData?.data?.attendances || []}
          todoData={todoData || []}
        />
      </div>
    </div>
  );
};

export default Home;
