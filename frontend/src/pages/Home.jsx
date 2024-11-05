import React from "react";
import MyCalendar from "../components/MyCalendar/Calendar";
import styles from "./../styles/Home/Home.module.css";
import Todo from "../components/Todo/Todo";

const Home = () => {
  return (
    <div className={styles.home}>
      <div className={styles.calendar}>
        <MyCalendar />
      </div>
      <div>
        <Todo />
      </div>
    </div>
  );
};

export default Home;
