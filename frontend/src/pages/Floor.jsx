import SeatButton from "../components/common/SeatButton";
import DonutChart from "../components/DonutChart";
import styles from "./../styles/Floor.module.css";

const Floor = () => {
  return (
    <div className={styles.container}>
      <div className={styles.left}>1</div>
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
export default Floor;
