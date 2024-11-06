import { useState } from "react";
import { Calendar, dateFnsLocalizer } from "react-big-calendar";
import dayjs from "dayjs";
import { format, parse, startOfWeek, getDay } from "date-fns";
import "react-big-calendar/lib/css/react-big-calendar.css";
import { ko } from "date-fns/locale";
import Toolbar from "./Toolbar";
import styles from "../../styles/Home/Calendar.module.css";
import "../../styles/Home/Calendar.css";

const locales = { ko };
const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek,
  getDay,
  locales,
});

// 날짜별 이벤트 데이터
const localEvents = [
  {
    start: new Date(2024, 10, 5),
    end: new Date(2024, 10, 5),
    title: "연차",
    type: "HOLIDAY",
  },
  {
    start: new Date(2024, 10, 10),
    end: new Date(2024, 10, 10),
    title: "조퇴",
    type: "SICK",
  },
  {
    start: new Date(2024, 10, 15),
    end: new Date(2024, 10, 15),
    title: "회의",
    type: "MEETING",
  },
  {
    start: new Date(2024, 10, 20),
    end: new Date(2024, 10, 20),
    title: "기타",
    type: "OTHER", // 기타 일정은 일정 제목 띄우기
  },
];

const MyCalendar = () => {
  const [events, setEvents] = useState(localEvents);

  const eventStyleGetter = (event) => {
    let backgroundColor = "";

    switch (event.type) {
      case "HOLIDAY":
        backgroundColor = "#FF6347"; // 예: 빨간색
        break;
      case "SICK":
        backgroundColor = "#A0A0A0"; // 예: 노란색
        break;
      case "MEETING":
        backgroundColor = "#52C41A"; // 예: 파란색
        break;
      case "OTHER":
        backgroundColor = "#104DEF";
        break;
      default:
        backgroundColor = "#808080"; // 예: 회색
    }

    return {
      style: {
        backgroundColor,
        color: "white",
      },
    };
  };
  // 빈 날짜 셀을 클릭했을 때의 동작
  const handleSelectSlot = (slotInfo) => {
    alert(`Selected slot: \nStart: ${slotInfo.start}\nEnd: ${slotInfo.end}`);
    // 필요한 동작 추가 가능
  };

  // 이벤트를 클릭했을 때의 동작
  const handleSelectEvent = (event) => {
    alert(`Selected event: ${event.title}`);
    // 필요한 동작 추가 가능
  };

  return (
    <Calendar
      localizer={localizer}
      defaultDate={new Date()}
      defaultView="month"
      events={events}
      className={styles.calendar}
      components={{
        toolbar: Toolbar,
      }}
      eventPropGetter={eventStyleGetter}
      selectable
      onSelectSlot={(slotInfo) => {
        alert(`일정추가할래유?`);
      }}
      onSelectEvent={(event) => {
        alert(`클릭함 ${event.title}`);
      }}
    />
  );
};

export default MyCalendar;
