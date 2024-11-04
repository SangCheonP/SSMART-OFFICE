import { Outlet } from "react-router-dom";
import styles from "./../styles/Seat.module.css";

const Seat = () => {
  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.date}>시간</div>
        <div className={styles.floor}>층수</div>
      </div>
      <Outlet />
    </div>
  );
};

export default Seat;
