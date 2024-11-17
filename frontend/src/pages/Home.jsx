import React, { useEffect, useState } from "react";
import MyCalendar from "@/components/MyCalendar/Calendar";
import styles from "@/styles/Home/Home.module.css";
import Todo from "@/components/Todo/Todo";
import useHomeStore from "@/store/useHomeStore";

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
    const month = today.getFullYear() * 100 + (today.getMonth() + 1);
    fetchCalendarData(month);

    // 캘린더 데이터 조회
    const day = today.getDate();
    fetchTodoData(month, day);
    fetchAttendanceData(month);
  }, [fetchCalendarData, fetchTodoData, fetchAttendanceData]);

  useEffect(() => {}, [calendarData, todoData, attendanceData]);

  const handleDateSelect = (date) => {
    const selected = new Date(date);
    setSelectedDate(selected);
    const month = date.getFullYear() * 100 + (date.getMonth() + 1);
    const day = date.getDate().toString().padStart(2, "0");
    fetchTodoData(month, day);
  };
  return (
    <div className={styles.home}>
      <div className={styles.calendar}>
        <MyCalendar
          monthData={calendarData.data || []}
          attendanceData={attendanceData.data || []}
          onDateSelect={handleDateSelect}
        />
      </div>
      <div>
        <Todo selectedDate={selectedDate} todoData={todoData || { data: [] }} />
      </div>
    </div>
  );
};

export default Home;
