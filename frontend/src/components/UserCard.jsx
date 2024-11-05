import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import ProfileImage from "./../assets/Login/ProfileImage.svg?react";
import styles from "./../styles/Header.module.css";
import Down from "./../assets/Common/Arrow_down.svg?react";

const UserCard = () => {
  const [isOpen, setIsOpen] = useState(false);
  const toggleMenu = () => setIsOpen((prev) => !prev);
  const navigate = useNavigate();
  const goMyPage = () => {
    navigate("/mypage");
  };

  return (
    <div className={styles.userCardContainer}>
      <div className={styles.userInfo} onClick={toggleMenu}>
        <ProfileImage />
        <div className={styles.info}>
          <div className={styles.userNumber}>S1234567</div>
          <div className={styles.nameTag}>
            <div className={styles.name}>복현우</div>
            <div>님</div>
            <Down className={styles.downIcon} />
          </div>
        </div>
      </div>

      {isOpen && (
        <div className={styles.dropdownMenu}>
          <div className={styles.menuItem} onClick={goMyPage}>
            마이페이지
          </div>
          <div className={styles.menuItem}>로그아웃</div>
        </div>
      )}
    </div>
  );
};

export default UserCard;
