import SeatButton from "@/components/common/SeatButton";
import DonutChart from "./DonutChart";
import styles from "@/styles/Seat/SeatingByFloor.module.css";
import PropTypes from "prop-types";
import SeatingStatus from "./SeatingStatus";

const SeatingByFloor = ({ floor, totalNumber }) => {
  if (floor == 1) {
    totalNumber = 3;
  } else if (floor == 2) {
    totalNumber = 5;
  } else if (floor == 3) {
    totalNumber = 7;
  } else {
    totalNumber = 2;
  }
  const occupant = [
    {
      seatInfoId: 1,
      userName: "홍길동",
      userPosition: "대리",
      userDuty: "프론트엔드",
      status: "IN_USE",
    },
    {
      number: 3,
      name: "이순신",
      position: "사원",
      role: "백엔드",
      status: "IN_USE",
    },
    {
      number: 4,
      name: "유관순",
      position: "부장",
      role: "프론트엔드",
      status: "NOT_OCCUPIED",
    },
    {
      number: 5,
      name: "김유신",
      position: "과장",
      role: "프론트엔드",
      status: "NOT_OCCUPIED",
    },
  ];

  return (
    <div className={styles.container}>
      <div className={styles.left}>
        <SeatingStatus
          floor={floor}
          occupant={occupant}
          totalNumber={totalNumber}
        />
      </div>
      <div className={styles.right}>
        <DonutChart number={occupant.length} totalNumber={totalNumber} />
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
  totalNumber: PropTypes.number.isRequired,
};
export default SeatingByFloor;
