import React from "react";
import styles from "@/styles/Home/Todo.module.css";

const ReadCard = ({ icon, iconBgColor, title, count, linkText, linkUrl }) => {
  return (
    <div className={styles.card}>
      <div
        className={styles.iconWrapper}
        style={{ backgroundColor: iconBgColor }}
      >
        {icon}
      </div>
      <div className={styles.content}>
        <div className={styles.title}>
          {title} {count}개
        </div>
        <a href={linkUrl} className={styles.link}>
          {linkText}
        </a>
      </div>
    </div>
  );
};

export default ReadCard;
