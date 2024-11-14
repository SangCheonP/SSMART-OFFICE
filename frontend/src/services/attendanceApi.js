import { queryOptions } from "@tanstack/react-query";
import api from "./api";

// 전체 사용자 목록 조회
export const fetchUserList = () => {
  return api.get("/users");
};

// 사원 일정 일별 조회
export const fetchUserTodo = (userId, month, day) => {
  return api.get(`/assignments`, {
    params: {
      userId: userId,
      month: month,
      day: day,
    },
  });
};

// 출퇴근 정보 조회(관리자)
export const fetchUserAttendance = (userId, month, day) => {
  return api.get(`/attendances`, {
    params: {
      userId: userId,
      month: month,
      day: day,
    },
  });
};
