import React from "react";
import styles from "../../styles/Home/WeekdayHeader.module.css";

const WeekdayHeader = ({ label }) => {
  const daysMap = {
    Sunday: "Sun",
    Monday: "Mon",
    Tuesday: "Tue",
    Wednesday: "Wed",
    Thursday: "Thu",
    Friday: "Fri",
    Saturday: "Sat",
  };
  return <div className={styles.weekdayHeader}>{daysMap[label] || label}</div>;
};

export default WeekdayHeader;
