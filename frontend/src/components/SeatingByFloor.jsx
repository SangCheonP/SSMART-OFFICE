import SeatButton from "./common/SeatButton";
import DonutChart from "./DonutChart";
import styles from "./../styles/Seat/SeatingByFloor.module.css";
import PropTypes from "prop-types";

const SeatStatus = ({ floor }) => {
  return (
    <div className={styles.container}>
      <div className={styles.left}>{floor}</div>
      <div className={styles.right}>
        <DonutChart />
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

SeatStatus.propTypes = {
  floor: PropTypes.string.isRequired,
};
export default SeatStatus;
