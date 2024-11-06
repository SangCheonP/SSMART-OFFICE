import React, { useState, useEffect } from "react";
import { Calendar, dateFnsLocalizer } from "react-big-calendar";
import { format, parse, startOfWeek, getDay } from "date-fns";
import "react-big-calendar/lib/css/react-big-calendar.css";
import { ko } from "date-fns/locale";
import Toolbar from "./Toolbar";
import styles from "@/styles/Home/Calendar.module.css";
import "@/styles/Home/Calendar.css";

const locales = { ko };
const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek,
  getDay,
  locales,
});

const CustomEvent = ({ event }) => {
  let circleColor = "";
  let displayTitle = "";

  switch (event.type) {
    case "HOLIDAY":
      circleColor = "#FF6347"; // 빨간색
      displayTitle = "연차";
      break;
    case "SICK":
      circleColor = "#FFD700"; // 노란색
      displayTitle = "조퇴";
      break;
    case "MEETING":
      circleColor = "#1E90FF"; // 파란색
      displayTitle = "회의";
      break;
    case "WORK":
      circleColor = "#32CD32"; // 녹색
      displayTitle = "TODO";
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
      <span style={{ fontSize: "13px" }}>{displayTitle}</span>
    </div>
  );
};

const MyCalendar = ({ monthData, onDateSelect }) => {
  const [events, setEvents] = useState([]);

  useEffect(() => {
    const formattedEvents = monthData.map((item) => ({
      start: new Date(item.date),
      end: new Date(item.date),
      title: item.name,
      type: item.type,
    }));
    setEvents(formattedEvents);
  }, [monthData]);

  return (
    <Calendar
      localizer={localizer}
      defaultDate={new Date()}
      defaultView="month"
      events={events}
      className={styles.calendar}
      components={{
        toolbar: Toolbar,
        event: CustomEvent,
      }}
      selectable
      onSelectSlot={(slotInfo) => onDateSelect(slotInfo.start)} // 클릭된 날짜 전달
      onSelectEvent={(event) => {
        alert(`클릭함 ${event.title}`);
      }}
    />
  );
};

export default MyCalendar;
