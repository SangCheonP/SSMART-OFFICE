import React, { useEffect, useState } from "react";
import MyCalendar from "@/components/MyCalendar/Calendar";
import styles from "@/styles/Home/Home.module.css";
import Todo from "@/components/Todo/Todo";
import { fetchCalendarData, fetchAttendanceData } from "@/services/homeApi";
const Home = () => {
  const [monthData, setMonthData] = useState([]);
  const [attendanceData, setAttendanceData] = useState([]);
  const [selectedDate, setSelectedDate] = useState(null);

  // // 임시 데이터
  // const monthData = [
  //   {
  //     date: "2024-11-29",
  //     name: "코드",
  //     type: "WORK",
  //   },
  // ];
  // const handleDateSelect = (date) => {
  //   setSelectedDate(date);
  // };

  // // 출퇴근 정보
  // const attendanceData = [
  //   {
  //     attendanceId: 1,
  //     userId: 1,
  //     attendanceType: "START",
  //     attendanceTime: "2024-11-06T09:40:00",
  //   },
  // ];

  useEffect(() => {
    const month = "202412"; // 조회할 month 값
    const day = "31"; // 조회할 day 값

    // 캘린더 정보 전체 조회
    fetchCalendarData(month, day)
      .then((response) => {
        console.log(response.data);
        setMonthData(response.data.data.attendances);
      })
      .catch((error) => {
        console.error("캘린더 데이터를 가져오는 중 오류 발생:", error);
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
        <Todo selectedDate={selectedDate} monthData={monthData} />
      </div>
    </div>
  );
};

export default Home;
