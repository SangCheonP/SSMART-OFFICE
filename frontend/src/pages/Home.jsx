import React, { useState } from "react";
import MyCalendar from "../components/MyCalendar/Calendar";
import styles from "./../styles/Home/Home.module.css";
import Todo from "../components/Todo/Todo";

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
      date: "2024-11-01",
      name: "연차",
      type: "HOLIDAY",
    },
    {
      date: "2024-11-29",
      name: "회의",
      type: "MEETING",
    },
    {
      date: "2024-11-30",
      name: "코드 리뷰 반영 및 수정",
      type: "WORK",
    },
  ];
  const handleDateSelect = (date) => {
    setSelectedDate(date);
  };
  return (
    <div className={styles.home}>
      <div className={styles.calendar}>
        <MyCalendar monthData={monthData} onDateSelect={handleDateSelect} />
      </div>
      <div>
        <Todo selectedDate={selectedDate} monthData={monthData} />
      </div>
    </div>
  );
};

export default Home;
