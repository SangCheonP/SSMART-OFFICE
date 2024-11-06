import { Outlet } from "react-router-dom";
import styles from "./../styles/Seat/Seat.module.css";
import FloorLink from "../components/common/FloorLink";
import TimeDisplay from "../components/Seat/TimeDisplay";

const Seat = () => {
  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <div className={styles.date}>
          <TimeDisplay />
        </div>
        <div className={styles.floor}>
          <FloorLink to="4" label="4F" className={styles.floor4} />
          <FloorLink to="3" label="3F" className={styles.floor3} />
          <FloorLink to="2" label="2F" className={styles.floor2} />
          <FloorLink to="1" label="1F" className={styles.floor1} />
        </div>
      </div>
      <Outlet />
    </div>
  );
};

export default Seat;
