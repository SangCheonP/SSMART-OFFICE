import React, { useState } from "react";
import MyCalendar from "@/components/MyCalendar/Calendar";
import styles from "@/styles/Home/Home.module.css";
import Todo from "@/components/Todo/Todo";

const Home = () => {
  const [selectedDate, setSelectedDate] = useState(null);
  // 임시 데이터

  const monthData = [
    {
      date: "2024-11-06",
      name: "코드",
      type: "WORK",
    },
    {
      date: "2024-11-06",
      name: "코드 리뷰 반영 및 수정2",
      type: "WORK",
    },
    {
      date: "2024-11-06",
      name: "코드 리뷰 반영 및 수정3",
      type: "WORK",
    },
    {
      date: "2024-11-06",
      name: "코드 리뷰 반영 및 수정4",
      type: "WORK",
    },
    {
      date: "2024-11-01",
      name: "연차",
      type: "HOLIDAY",
    },
    {
      date: "2024-11-04",
      name: "회의",
      type: "MEETING",
    },
    {
      date: "2024-11-04",
      name: "코드 리뷰 반영 및 수정",
      type: "WORK",
    },
  ];
  const handleDateSelect = (date) => {
    setSelectedDate(date);
  };

  // 출퇴근 정보
  const attendanceData = [
    {
      attendanceId: 1,
      userId: 1,
      attendanceType: "START",
      attendanceTime: "2024-11-06T09:40:00",
    },
    {
      attendanceId: 2,
      userId: 1,
      attendanceType: "END",
      attendanceTime: "2024-11-06T18:00:00",
    },
    {
      attendanceId: 3,
      userId: 1,
      attendanceType: "START",
      attendanceTime: "2024-11-01T09:00:00",
    },
    {
      attendanceId: 4,
      userId: 1,
      attendanceType: "END",
      attendanceTime: "2024-11-01T21:00:00",
    },
    {
      attendanceId: 3,
      userId: 1,
      attendanceType: "START",
      attendanceTime: "2024-11-04T09:00:00",
    },
    {
      attendanceId: 4,
      userId: 1,
      attendanceType: "END",
      attendanceTime: "2024-11-04T18:30:00",
    },
  ];
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
