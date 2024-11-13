import api from "./api";

// 캘린더 정보 전체 조회
export const fetchCalendarData = (month, day) => {
  return api.get(`/api/v1/assignments?month=${month}&day=${day}`);
};

// 내 출퇴근 정보 조회
export const fetchAttendanceData = (month, day) => {
  return api.get(`/api/v1/attendances/me?month=${month}&day=${day}`);
};
