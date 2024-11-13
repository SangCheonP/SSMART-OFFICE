import React, { useEffect, useState } from "react";
import MyCalendar from "@/components/MyCalendar/Calendar";
import styles from "@/styles/Home/Home.module.css";
import Todo from "@/components/Todo/Todo";
import {
  fetchCalendarData,
  fetchTodoData,
  fetchAttendanceData,
} from "@/services/homeApi";
const Home = () => {
  const [monthData, setMonthData] = useState([]);
  const [attendanceData, setAttendanceData] = useState([]);
  const [todoData, setTodoData] = useState([]);
  const [selectedDate, setSelectedDate] = useState(null);

  useEffect(() => {
    const month = "202412"; // 조회할 month 값
    const day = "31"; // 조회할 day 값

    // 캘린더 일정 월별 조회
    fetchCalendarData(month, day)
      .then((response) => {
        console.log(response.data.data.attendances);
        setMonthData(response.data.data.attendances);
      })
      .catch((error) => {
        console.error("캘린더 데이터를 가져오는 중 오류 발생:", error);
      });

    // 캘린더 일정 일별 조회
    fetchTodoData(month, day)
      .then((response) => {
        console.log(response.data.data);
        setTodoData(response.data.data);
      })
      .catch((error) => {
        console.error("일별 일정을 가져오는 중 오류 발생:", error);
      });

    // 출퇴근 정보 조회
    fetchAttendanceData(month, day)
      .then((response) => {
        console.log(response.data);
        setAttendanceData(response.data.data);
      })
      .catch((error) => {
        console.error("출퇴근 데이터를 가져오는 중 오류 발생:", error);
      });
  }, []);

  const handleDateSelect = (date) => {
    setSelectedDate(date);
  };

  return (
    <div className={styles.home}>
      <div className={styles.calendar}>
        <MyCalendar
          monthData={monthData}
          attendanceData={attendanceData}
          onDateSelect={handleDateSelect}
        />
      </div>
      <div>
        <Todo
          selectedDate={selectedDate}
          monthData={monthData}
          todoData={todoData}
        />
      </div>
    </div>
  );
};

export default Home;
