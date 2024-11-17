import { useNavigate } from "react-router-dom";
import { Outlet, useParams } from "react-router-dom";
import styles from "@/styles/Seat/Seat.module.css";
import FloorLink from "@/components/common/FloorLink";
import TimeDisplay from "@/components/Seat/TimeDisplay";
import { useEffect, useState } from "react";

import { fetchSeats } from "@/services/seatAPI";

import { handleError } from "@/utils/errorHandler";

const Seat = () => {
  const [seats, setSeats] = useState(null);
  const { floor } = useParams();
  const navigate = useNavigate();

  useEffect(() => {
    const loadSeats = async () => {
      try {
        const data = await fetchSeats(floor);
        setSeats(data);
      } catch (e) {
        handleError(e);
      }
    };
    if (floor) {
      loadSeats();
    } else {
      navigate("/seat/1", { replace: true });
    }
  }, [floor, navigate]);

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
      <Outlet context={{ seats, floor }} />
    </div>
  );
};

export default Seat;
