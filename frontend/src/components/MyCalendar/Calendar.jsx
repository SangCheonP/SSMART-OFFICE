import React, { useState } from "react";
import { Calendar, dateFnsLocalizer } from "react-big-calendar";
import dayjs from "dayjs";
import { format, parse, startOfWeek, getDay } from "date-fns";
import "react-big-calendar/lib/css/react-big-calendar.css";
import { ko } from "date-fns/locale";
import Toolbar from "./Toolbar";
import WeekdayHeader from "./WeekdayHeader";

const locales = { ko };
const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek,
  getDay,
  locales,
});

const MyCalendar = () => {
  const [events, setEvents] = useState([
    {
      start: new Date(),
      end: new Date(dayjs().add(1, "day").toDate()),
      title: "Sample Event",
    },
    // 필요한 추가 이벤트를 여기에 넣을 수 있어요.
  ]);

  return (
    <Calendar
      localizer={localizer}
      defaultDate={new Date()}
      defaultView="month"
      events={events}
      style={{ height: "78vh", width: "80vh" }}
      components={{
        toolbar: Toolbar,
      }}
    />
  );
};

export default MyCalendar;
