import PropTypes from "prop-types";
import styles from "@/styles/Seat/SeatButton.module.css";

const SeatButton = ({ color, content }) => {
  return (
    <div className={styles.container}>
      <div className={styles.content} data-color={color}>
        {content}
      </div>
    </div>
  );
};

SeatButton.propTypes = {
  color: PropTypes.string.isRequired,
  content: PropTypes.string.isRequired,
};

export default SeatButton;
