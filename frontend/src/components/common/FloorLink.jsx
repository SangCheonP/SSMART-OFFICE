import { NavLink } from "react-router-dom";
import styles from "@/styles/Seat/FloorLink.module.css";
import PropTypes from "prop-types";

const FloorLink = ({ to, label, className }) => {
  return (
    <NavLink
      to={to}
      className={({ isActive }) =>
        isActive ? `${className} ${styles.active}` : className
      }
    >
      {label}
    </NavLink>
  );
};

FloorLink.propTypes = {
  to: PropTypes.string.isRequired,
  label: PropTypes.string.isRequired,
  className: PropTypes.string.isRequired,
  isIndex: PropTypes.bool,
};

FloorLink.defaultProps = {
  isIndex: false,
};

export default FloorLink;
