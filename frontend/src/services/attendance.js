import api from "./api";

// 전체 사용자 목록 조회
export const fetchUserList = () => {
  return api.get("/api/v1/users");
};
