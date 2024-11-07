import Home from "@/assets/Menu/SSMART OFFICE.svg?react";
import Profile from "@/assets/Common/Profile.png";

import styles from "@/styles/MyPage/MyPage.module.css";

const MyInfo = () => {
  return (
    <div className={styles.container}>
      <div className={styles.left}>
        <Home className={styles.image} />
      </div>
      <div className={styles.right}>
        <div className={styles.imageBox}>
          <img src={Profile} alt="" />
        </div>
        <div className={styles.content}>
          <div className={styles.title}>
            <p className={styles.subTitle}>김진기 사장님</p>
            <p className={styles.subTitle1}>반갑습니다!</p>
          </div>
          <div>test@gmail.com</div>
          <div>010-0101-1010</div>
          <div className={styles.welfare}>
            <p className={styles.subWelfare}>복지포인트 : </p>
            <p className={styles.subWelfare1}>34023</p>
          </div>
          <div className={styles.buttonBox}>
            <button className={`${styles.modifyButton} ${styles.button}`}>
              개인정보 수정
            </button>
            <button className={`${styles.changeButton} ${styles.button}`}>
              비밀번호 변경
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
export default MyInfo;
