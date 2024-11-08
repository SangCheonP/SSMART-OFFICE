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
  let textColor = "#000";

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

    // 시간순 정렬이라 일정은 임의로 지정
    const formattedMonthEvents = monthData.map((item) => ({
      start: new Date(new Date(item.date).setHours(23, 59, 0)),
      end: new Date(new Date(item.date).setHours(23, 59, 0)),
      title: item.name,
      type: item.type,
    }));

    // 출근/퇴근 이벤트가 상단에 오도록 정렬
    const allEvents = [...formattedAttendanceEvents, ...formattedMonthEvents];

    allEvents.sort((a, b) => {
      if (a.type === "START") return -1;
      if (b.type === "START") return 1;
      if (a.type === "END") return -1;
      if (b.type === "END") return 1;
      return a.start - b.start;
    });

    setEvents(allEvents);
  }, [monthData, attendanceData]);

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
      onSelectSlot={(slotInfo) => onDateSelect(slotInfo.start)}
      onSelectEvent={(event) => {
        alert(`클릭함 ${event.title}`);
      }}
    />
  );
};

export default MyCalendar;
