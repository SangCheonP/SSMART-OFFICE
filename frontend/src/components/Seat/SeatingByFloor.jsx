import { PropTypes } from "prop-types";
import SeatButton from "@/components/common/SeatButton";
import DonutChart from "./DonutChart";
import SeatingStatus from "./SeatingStatus";

import styles from "@/styles/Seat/SeatingByFloor.module.css";
import { useOutletContext } from "react-router-dom";

const SeatingByFloor = ({ floor }) => {
  const seats = useOutletContext();

  return (
    <div className={styles.container}>
      <div className={styles.left}>
        <SeatingStatus floor={floor} seats={seats} totalNumber={6} />
      </div>
      <div className={styles.right}>
        <DonutChart number={seats.length} totalNumber={6} />
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
  floor: PropTypes.string.isRequired,
};

export default SeatingByFloor;
