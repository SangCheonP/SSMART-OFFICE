import React, { useState } from "react";
import { Calendar, dateFnsLocalizer } from "react-big-calendar";
import { format, parse, startOfWeek, getDay, isSameDay } from "date-fns";
import "react-big-calendar/lib/css/react-big-calendar.css";
import { ko, enUS } from "date-fns/locale";
import AttendanceToolbar from "./AttendanceToolbar";
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

const MyCalendar = ({ userId, attendanceData, userTodoData, onDateSelect }) => {
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [selectedTodos, setSelectedTodos] = useState([]);
  const [selectedAttendance, setSelectedAttendance] = useState([]);

  const handleDateChange = (slotInfo) => {
    setSelectedDate(slotInfo.start);

    // 선택한 날짜에 맞는 데이터 로드
    const month =
      slotInfo.start.getFullYear() * 100 + (slotInfo.start.getMonth() + 1);
    const day = slotInfo.start.getDate();

    onDateSelect(slotInfo.start);
  };

  return (
    <div className={styles.calendar_form}>
      <Calendar
        localizer={localizer}
        events={userTodoData.map((todo) => ({
          title: todo.name,
          start: new Date(todo.assignmentDate),
          end: new Date(todo.assignmentDate),
        }))}
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
    </div>
  );
};

export default MyCalendar;
