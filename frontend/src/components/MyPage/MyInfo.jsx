import styles from "./../../styles/MyPage/MyPage.module.css";
import Home from "./../../assets/Menu/SSMART OFFICE.svg?react";

const MyInfo = () => {
  return (
    <div className={styles.container}>
      <div className={styles.left}>
        <Home className={styles.image} />
      </div>
      <div className={styles.right}>somthing</div>
    </div>
  );
};
export default MyInfo;
