import { PropTypes } from "prop-types";
import SeatButton from "@/components/common/SeatButton";
import DonutChart from "./DonutChart";
import SeatingStatus from "./SeatingStatus";

import styles from "@/styles/Seat/SeatingByFloor.module.css";
import { useOutletContext } from "react-router-dom";

const SeatingByFloor = () => {
  const { floor, seats } = useOutletContext();

  let number = 0;
  for (let i = 0; i < seats?.length; i++) {
    if (
      seats[i].status === "IN_USE" ||
      seats[i].status === "UNAVAILABLE" ||
      seats[i].status === "NOT_OCCUPIED"
    ) {
      number++;
    }
  }

  return (
    <div className={styles.container}>
      <div className={styles.left}>
        {seats && <SeatingStatus floor={floor} seats={seats} totalNumber={6} />}
      </div>
      <div className={styles.right}>
        {seats && <DonutChart number={number} totalNumber={6} />}
        <div className={styles.buttonContainer}>
          <SeatButton color="grey" content="사용 가능" />
          <SeatButton color="blue" content="사용중" />
          <SeatButton color="pink" content="자리비움" />
          <SeatButton color="darkGrey" content="사용불가" />
        </div>
      </div>
    </div>
  );
};

SeatingByFloor.propTypes = {
  floor: PropTypes.string,
};

export default SeatingByFloor;
