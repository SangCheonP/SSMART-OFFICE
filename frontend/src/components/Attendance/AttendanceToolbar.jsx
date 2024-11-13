import React from "react";
import Left from "@/assets/Common/Chevron-left.svg?react";
import Right from "@/assets/Common/Chevron-right.svg?react";
import styles from "@/styles/Attendance/Member.module.css";
export default function AttendanceToolbar(props) {
  const { date } = props;

  const navigate = (action) => {
    props.onNavigate(action);
  };
  return (
    <div className={styles.toolbar}>
      <span className={styles.toolbarGroup}>
        <div className={styles.dateGroup}>
          <button
            type="button"
            onClick={() => navigate("PREV")}
            className={styles.leftButton}
          >
            <Left />
          </button>
          <span>{`${date.getFullYear()}. ${date.getMonth() + 1}`}</span>

          <button
            type="button"
            onClick={() => navigate("NEXT")}
            className={styles.rightButton}
          >
            <Right />
          </button>
        </div>
      </span>
    </div>
  );
}
