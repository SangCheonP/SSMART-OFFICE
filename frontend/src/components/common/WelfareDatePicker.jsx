import DatePicker from "react-datepicker";
import styles from "@/styles/MyPage/WelfareDatePicker.module.css";
import { useState } from "react";

const WelfareDatePicker = () => {
  const [selectedDate, setSelectedDate] = useState(new Date());

  return (
    <DatePicker
      dateFormat="yyyy.MM.dd"
      shouldCloseOnSelect
      minDate={new Date("2000-01-01")} // minDate 이전 날짜 선택 불가
      maxDate={new Date()} // maxDate 이후 날짜 선택 불가
      selected={selectedDate}
      onChange={(date) => setSelectedDate(date)}
      className={styles.datePicker}
    />
  );
};

export default WelfareDatePicker;
