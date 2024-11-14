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
    const month = "202412"; // 조회할 month 값
    const day = "31"; // 조회할 day 값

    // 캘린더 데이터 조회
    fetchCalendarData(month);
    fetchTodoData(month, day);
    fetchAttendanceData(month, day);
  }, [fetchCalendarData, fetchTodoData, fetchAttendanceData]);

  const handleDateSelect = (date) => {
    setSelectedDate(date);
  };

  return (
    <div className={styles.home}>
      <div className={styles.calendar}>
        <MyCalendar
          monthData={calendarData?.data?.attendances || []}
          attendanceData={attendanceData || []}
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
