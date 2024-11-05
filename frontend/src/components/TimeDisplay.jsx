import dayjs from "dayjs";
import "dayjs/locale/ko";
import { useEffect, useState } from "react";
dayjs.locale("ko");

const TimeDisplay = () => {
  const [today, setToday] = useState(dayjs());

  useEffect(() => {
    const updateCurrentTime = () => setToday(dayjs());
    const interval = setInterval(updateCurrentTime, 1000);
    return () => clearInterval(interval);
  }, []);

  return <>{today.format("YYYY.MM.DD ddd요일 HH:mm:ss")}</>;
};
export default TimeDisplay;
