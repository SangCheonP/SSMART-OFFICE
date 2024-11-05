import React, { useState } from "react";
import { Calendar, momentLocalizer } from "react-big-calendar";
import moment from "moment";
import "react-big-calendar/lib/css/react-big-calendar.css";
import Toolbar from "./Toolbar";

const localizer = momentLocalizer(moment);

const MyCalendar = () => {
  moment.locale("ko-KR");
  const [events, setEvents] = useState([
    {
      start: new Date(),
      end: new Date(moment().add(1, "days")),
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
      style={{ height: 766, width: 812 }}
      components={{
        toolbar: Toolbar,
      }}
    />
  );
};

export default MyCalendar;
