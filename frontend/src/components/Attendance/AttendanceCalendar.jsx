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

const MyCalendar = ({ userId, attendanceData, userTodoData, onDateSelect }) => {
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [selectedTodos, setSelectedTodos] = useState([]);
  const [selectedAttendance, setSelectedAttendance] = useState([]);

  const handleDateChange = (slotInfo) => {
    setSelectedDate(slotInfo.start);
    onDateSelect(slotInfo.start); // 부모 컴포넌트에서 출퇴근 정보 다시 조회

    // 선택된 날짜의 일정 필터링
    const filteredTodos = userTodoData.filter((todo) =>
      isSameDay(new Date(todo.assignmentDate), slotInfo.start)
    );
    setSelectedTodos(filteredTodos); // TodoList에 보여줄 일정 설정

    // 선택된 날짜의 출퇴근 정보 필터링
    const filteredAttendance = attendanceData.filter((attendance) =>
      isSameDay(new Date(attendance.attendanceTime), slotInfo.start)
    );
    setSelectedAttendance(filteredAttendance);
  };

  // // 예제 이벤트 데이터
  // const events = [
  //   {
  //     title: "코드 리뷰 반영 및 수정",
  //     start: new Date(2024, 10, 15, 10, 53),
  //     end: new Date(2024, 10, 15, 11, 53),
  //   },
  //   {
  //     title: "사업 초안 보고서 작성",
  //     start: new Date(2024, 10, 3, 14, 16),
  //     end: new Date(2024, 10, 3, 15, 16),
  //   },
  //   {
  //     title: "팀 미팅",
  //     start: new Date(2024, 10, 16, 15, 40),
  //     end: new Date(2024, 10, 17, 16, 40),
  //   },
  // ];

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
      {/* TodoList 컴포넌트에 selectedTodos, 출퇴근 정보를 전달 */}
      <TodoList
        monthData={[
          ...selectedAttendance.map((attendance) => ({
            name: `${attendance.attendanceType} - ${new Date(
              attendance.attendanceTime
            ).toLocaleTimeString()}`,
          })),
          ...selectedTodos.map((todo) => ({
            name: todo.name,
            description: todo.description,
          })),
        ]}
      />
    </div>
  );
};

export default MyCalendar;
