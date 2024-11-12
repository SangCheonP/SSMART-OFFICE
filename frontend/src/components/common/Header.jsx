import { useLocation } from "react-router-dom";
import Right from "@/assets/Common/Right.svg?react";
import styles from "@/styles/Header.module.css";
import UserCard from "@/components/UserCard";

const Header = () => {
  const location = useLocation();

  const pageTitle = {
    "/seat": "좌석 현황",
    "/mypage": "마이페이지",
    "/message": "사내 메시지",
    "/attendance": "사원 관리",
  };

  const currentKey = Object.keys(pageTitle).find((key) =>
    location.pathname.startsWith(key)
  );
  const currentTitle = currentKey ? pageTitle[currentKey] : "홈";

  return (
    <div className={styles.header}>
      <div className={styles.leftSection}>
        <div className={styles.department}>
          <span>쓰마트오피쓰</span>
          <Right className={styles.rightIcon} />
          <span>{currentTitle}</span>
        </div>
        <div className={styles.menu}>{currentTitle}</div>
      </div>
      <UserCard />
    </div>
  );
};

export default Header;
