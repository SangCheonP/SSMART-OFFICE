import React, { useState } from "react";
import { Calendar, dateFnsLocalizer } from "react-big-calendar";
import { format, parse, startOfWeek, getDay, isSameDay } from "date-fns";
import "react-big-calendar/lib/css/react-big-calendar.css";
import { ko, enUS } from "date-fns/locale";
import AttendanceToolbar from "./AttendanceToolbar";
import TodoList from "@/components/Todo/TodoList";
import styles from "@/styles/Attendance/Member.module.css";

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

const MyCalendar = () => {
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [selectedTodos, setSelectedTodos] = useState([]);

  const handleDateChange = (slotInfo) => {
    setSelectedDate(slotInfo.start);

    // 선택된 날짜의 이벤트 필터링
    const filteredTodos = events.filter((event) =>
      isSameDay(event.start, slotInfo.start)
    );
    setSelectedTodos(filteredTodos);
  };

  // 예제 이벤트 데이터
  const events = [
    {
      title: "코드 리뷰 반영 및 수정",
      start: new Date(2024, 10, 15, 10, 53),
      end: new Date(2024, 10, 15, 11, 53),
    },
    {
      title: "사업 초안 보고서 작성",
      start: new Date(2024, 10, 3, 14, 16),
      end: new Date(2024, 10, 3, 15, 16),
    },
    {
      title: "팀 미팅",
      start: new Date(2024, 10, 16, 15, 40),
      end: new Date(2024, 10, 17, 16, 40),
    },
  ];

  return (
    <div className={styles.calendar_form}>
      <Calendar
        localizer={localizer}
        events={events}
        startAccessor="start"
        endAccessor="end"
        onSelectSlot={handleDateChange}
        selectable
        defaultView="month"
        defaultDate={selectedDate}
        formats={formats}
        components={{
          toolbar: AttendanceToolbar,
        }}
      />
      {/* TodoList 컴포넌트에 selectedTodos를 전달 */}
      <TodoList
        monthData={selectedTodos.map((event) => ({ name: event.title }))}
      />
    </div>
  );
};

export default MyCalendar;
