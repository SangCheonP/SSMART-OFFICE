import api from "./api";
import { handleError } from "@/utils/errorHandler";

// 캘린더 일정 월별 조회
export const fetchCalendarData = (month) => {
  return api.get(`/assignments/summary`, {
    params: { month: month },
  });
};

// 캘린더 일정 일별 조회
export const fetchTodoData = (month, day) => {
  const formattedMonth = String(month).padStart(2, "0");
  const formattedDay = String(day).padStart(2, "0");
  return api.get(`/assignments`, {
    params: { month: formattedMonth, day: formattedDay },
  });
};

// 내 출퇴근 정보 조회
export const fetchAttendanceData = (month, day = null) => {
  const params = day ? { month: month, day: day } : { month: month };
  return api.get(`/attendances/me`, { params });
};

// 일정 추가
export const addCalendarEvent = (
  assignmentName,
  assignmentDate,
  assignmentType,
  description
) => {
  return api.post("/assignments", {
    name: assignmentName,
    date: assignmentDate,
    type: assignmentType,
    description: description,
    completed: false,
  });
};

// 일정 완료
export const checkEvent = (assignmentId) => {
  return api.patch(`/assignments/${assignmentId}`);
};
