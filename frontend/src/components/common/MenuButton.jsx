import Home from "@/assets/Menu/Home.svg?react";
import Logout from "@/assets/Menu/Logout.svg?react";
import Message from "@/assets/Menu/Message.svg?react";
import Mypage from "@/assets/Menu/Mypage.svg?react";
import Seat from "@/assets/Menu/Seat.svg?react";
import Attendance from "@/assets/Menu/Attendance.svg?react";

import styles from "@/styles/Menu/MenuButton.module.css";

import classNames from "classnames";
import PropTypes from "prop-types";

const MenuButton = ({ type, content, isActive, isLogout }) => {
  let Icon = null;
  switch (type) {
    case "Home":
      Icon = Home;
      break;
    case "Logout":
      Icon = Logout;
      break;
    case "Message":
      Icon = Message;
      break;
    case "Mypage":
      Icon = Mypage;
      break;
    case "Seat":
      Icon = Seat;
      break;
    case "Attendance":
      Icon = Attendance;
      break;
  }
  const buttonClass = classNames(styles.button, classNames, {
    [styles.activeButton]: isActive,
    [styles.logoutButton]: isLogout,
  });
  return (
    <div className={buttonClass}>
      {Icon && <Icon className={styles.image} />}
      <div className={styles.text}>{content}</div>
    </div>
  );
};

MenuButton.propTypes = {
  type: PropTypes.string.isRequired,
  content: PropTypes.string.isRequired,
  isActive: PropTypes.bool.isRequired,
  isLogout: PropTypes.bool.isRequired,
};

export default MenuButton;
