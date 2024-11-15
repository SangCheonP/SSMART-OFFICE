import { useState } from "react";
import { useNavigate } from "react-router-dom";
import ProfileImage from "@/assets/Login/ProfileImage.svg?react";
import styles from "@/styles/Header.module.css";
import Down from "@/assets/Common/Arrow_down.svg?react";
import { setLogout } from "@/services/authAPI";
import useAuthStore from "@/store/useAuthStore";
import useMyInfoStore from "@/store/useMyInfoStore";

const UserCard = () => {
  const clearAuth = useAuthStore((state) => state.clearAuth);

  const { name, employeeNumber, profileImageUrl } = useMyInfoStore();

  const [isOpen, setIsOpen] = useState(false);
  const toggleMenu = () => setIsOpen((prev) => !prev);
  const navigate = useNavigate();

  const goMyPage = () => {
    navigate("/mypage");
  };

  const handleLogoutClick = () => {
    setLogout(clearAuth, navigate);
  };

  return (
    <div className={styles.userCardContainer}>
      <div className={styles.userInfo} onClick={toggleMenu}>
        {profileImageUrl ? (
          <img src={profileImageUrl} className={styles.profileImage} />
        ) : (
          <ProfileImage />
        )}
        <div className={styles.info}>
          <div className={styles.userNumber}>{employeeNumber}</div>
          <div className={styles.nameTag}>
            <div className={styles.name}>{name}</div>
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
          <div className={styles.menuItem} onClick={handleLogoutClick}>
            로그아웃
          </div>
        </div>
      )}
    </div>
  );
};

export default UserCard;
