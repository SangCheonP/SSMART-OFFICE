import React from "react";
import MyCalendar from "../components/MyCalendar/Calendar";
import styles from "./../styles/Home.module.css";

const Home = () => {
  return (
    <div className={styles.home}>
      <div className={styles.calendar}></div>
      <MyCalendar />
    </div>
  );
};

export default Home;
