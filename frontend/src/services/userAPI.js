import api from "@/services/api";
import { handleError } from "@/utils/errorHandler";
// 유저 등록
export const registerUser = async (userData) => {
  try {
    const response = await api.post("/users", userData);
    return response.data;
  } catch (error) {
    handleError(error);
  }
};

// 유저 정보 가져오기
export const getUser = async (userId) => {
  try {
    const response = await api.get(`/users/${userId}`);
    return response.data;
  } catch (error) {
    handleError(error);
  }
};

// 유저 수정
export const modifyUser = async (userId, userData) => {
  try {
    const response = await api.patch(`/users/${userId}`, userData);
    return response.data;
  } catch (error) {
    handleError(error);
  }
};
