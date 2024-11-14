import { useState, useEffect } from "react";
import { Calendar, dateFnsLocalizer } from "react-big-calendar";
import { format, parse, startOfWeek, getDay } from "date-fns";
import "react-big-calendar/lib/css/react-big-calendar.css";
import { ko, enUS } from "date-fns/locale";
import Toolbar from "./Toolbar";
import styles from "@/styles/Home/Calendar.module.css";
import "@/styles/Home/Calendar.css";
import useHomeStore from "@/store/useHomeStore";

const locales = { ko, enUS };
const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek,
  getDay,
  locales,
});
const formats = {
  weekdayFormat: (date, culture, localizer) =>
    localizer.format(date, "EEE", culture === "ko" ? enUS : culture),
};

const CustomEvent = ({ event }) => {
  let circleColor = "";
  let displayTitle = "";
  let textColor = "#000";

  switch (event.type) {
    case "ANNUAL_LEAVE":
      circleColor = "#FF6347"; // 빨간색
      displayTitle = "연차";
      break;
    case "EARLY_LEAVE":
      circleColor = "#FFD700"; // 노란색
      displayTitle = "조퇴";
      break;
    case "MEETING":
      circleColor = "#1E90FF"; // 파란색
      displayTitle = "회의";
      break;
    case "TASK":
      circleColor = "#32CD32"; // 녹색
      displayTitle = "TODO";
      break;
    case "OTHER":
      circleColor = "#A0A0A0"; // 회색
      displayTitle = "기타";
      break;
    case "START":
      displayTitle = `출근 ${format(event.start, "HH:mm")}`;
      textColor = "#6D91F2";
      break;
    case "END":
      displayTitle = `퇴근 ${format(event.start, "HH:mm")}`;
      textColor = "#6D91F2";
      break;
    default:
      circleColor = "#808080"; // 기본 회색
  }

  return (
    <div style={{ display: "flex", alignItems: "center" }}>
      <span
        style={{
          display: "inline-block",
          width: "8px",
          height: "8px",
          borderRadius: "50%",
          backgroundColor: circleColor,
          marginRight: "5px",
        }}
      ></span>
      <span style={{ fontSize: "13px", color: textColor }}>{displayTitle}</span>
    </div>
  );
};

const MyCalendar = ({ monthData, attendanceData, onDateSelect }) => {
  const [events, setEvents] = useState([]);

  useEffect(() => {
    const formattedAttendanceEvents = attendanceData.map((item) => ({
      start: new Date(item.attendanceTime),
      end: new Date(item.attendanceTime),
      title: item.attendanceType === "START" ? "출근" : "퇴근",
      type: item.attendanceType,
    }));

    const formattedMonthEvents = monthData.map((item) => ({
      start: new Date(new Date(item.date).setHours(23, 59, 0)),
      end: new Date(new Date(item.date).setHours(23, 59, 0)),
      title: item.name,
      type: item.type,
    }));

    setEvents([...formattedAttendanceEvents, ...formattedMonthEvents]);
  }, [monthData, attendanceData]);

  return (
    <Calendar
      localizer={localizer}
      defaultDate={new Date()}
      defaultView="month"
      events={events}
      className={styles.calendar}
      formats={formats}
      components={{
        toolbar: Toolbar,
        event: CustomEvent,
      }}
      selectable
      onSelectSlot={(slotInfo) => onDateSelect(slotInfo.start)}
      onSelectEvent={(event) => {
        alert(`클릭함 ${event.title}`);
      }}
    />
  );
};

export default MyCalendar;
