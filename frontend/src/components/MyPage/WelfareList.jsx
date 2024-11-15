import styles from "@/styles/MyPage/WelfareList.module.css";
import WelfareDatePicker from "@/components/common/WelfareDatePicker";

const WelfareList = () => {
  const num = 20000;
  return (
    <>
      <div className={styles.title}>복지 포인트 내역</div>
      <div className={styles.table}>
        <div className={styles.dateBox}>
          <WelfareDatePicker />
          {/* <input
            type="date"
            className={`${styles.datePicker} ${styles.firstDate} `}
          /> */}
          <div>~</div>
          {/* <input type="date" className={styles.datePicker} /> */}
          <WelfareDatePicker />
          <button className={styles.welfareSearch}>검색</button>
        </div>
        <div className={styles.tableHeader}>
          <div className={styles.first}>설명</div>
          <div>날짜</div>
          <div>결제 금액</div>
          <div>잔여</div>
        </div>
        <div className={styles.content}>
          <div className={styles.first}>밥 먹었음</div>
          <div>2024.11.13</div>
          <div>-300000</div>
          <div>200</div>
        </div>
        <div className={styles.content}>
          <div className={styles.first}>밥 먹었음</div>
          <div>2024.11.13</div>
          <div>+300000</div>
          <div>{num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",")}</div>
        </div>
      </div>
    </>
  );
};
export default WelfareList;
