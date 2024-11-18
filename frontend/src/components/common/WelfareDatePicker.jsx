import { PropTypes } from "prop-types";
import DatePicker from "react-datepicker";
import styles from "@/styles/MyPage/WelfareDatePicker.module.css";

const WelfareDatePicker = ({ value, onChange }) => {
  return (
    <DatePicker
      dateFormat="yyyy.MM.dd"
      shouldCloseOnSelect
      minDate={new Date("2000-01-01")} // minDate 이전 날짜 선택 불가
      maxDate={new Date()} // maxDate 이후 날짜 선택 불가
      selected={value}
      onChange={onChange}
      className={styles.datePicker}
      value={value}
    />
  );
};

WelfareDatePicker.propTypes = {
  value: PropTypes.instanceOf(Date),
  onChange: PropTypes.func,
};

export default WelfareDatePicker;
