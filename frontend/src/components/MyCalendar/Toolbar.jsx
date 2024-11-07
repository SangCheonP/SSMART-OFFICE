import React from "react";
import styles from "@/styles/Home/Toolbar.module.css";
import Left from "@/assets/Common/Arrow_left.svg?react";
import Right from "@/assets/Common/Arrow_right.svg?react";
import Line from "@/assets/Common/Vertical_line.svg?react";

export default function Toolbar(props) {
  const { date } = props;

  const navigate = (action) => {
    props.onNavigate(action);
  };

  return (
    <div className={styles.toolbar}>
      <span className={styles.toolbarGroup}>
        <div className={styles.title}>나의 일정</div>
        <Line />
        <span className={styles.dateGroup}>
          {`${date.getFullYear()}. ${date.getMonth() + 1}`}
        </span>
        <button
          type="button"
          onClick={() => navigate("PREV")}
          className={styles.leftButton}
        >
          <Left />
        </button>
        <button
          type="button"
          onClick={() => navigate("NEXT")}
          className={styles.rightButton}
        >
          <Right />
        </button>
      </span>
    </div>
  );
}
