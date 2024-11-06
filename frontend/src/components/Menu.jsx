import MenuButton from "./../components/common/MenuButton";
import HomeIcon from "./../assets/Menu/SSMART OFFICE.svg?react";
import { NavLink } from "react-router-dom";
import styles from "./../styles/Menu/Menu.module.css";
import PropTypes from "prop-types";

const NavItem = ({ link, type, content }) => {
  return (
    <NavLink to={link}>
      {({ isActive }) => {
        return (
          <MenuButton
            type={type}
            content={content}
            isActive={isActive}
            isLogout={type === "Logout"}
          />
        );
      }}
    </NavLink>
  );
};

const Menu = () => {
  return (
    <>
      <NavLink to="/" className={styles.temp}>
        <HomeIcon className={styles.icon} />
      </NavLink>
      <NavItem link="/" type="Home" content="홈" />
      <NavItem link="/seat" type="Seat" content="좌석 현황" />
      <NavItem link="/message" type="Message" content="사내 메시지" />
      <NavItem link="/mypage" type="Mypage" content="마이페이지" />
      <NavItem link="/logout" type="Logout" content="로그아웃" />
    </>
  );
};

NavItem.propTypes = {
  link: PropTypes.string.isRequired,
  type: PropTypes.string.isRequired,
  content: PropTypes.string.isRequired,
};

export default Menu;
