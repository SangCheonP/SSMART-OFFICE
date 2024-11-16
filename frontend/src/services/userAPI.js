import api from "@/services/api";
// 유저 등록
export const registerUser = async (userData) => {
  try {
    console.log("유저 API : ", userData);
    const response = await api.post("/users", userData);
    console.log("유저 API : ", response.data);
    return response.data;
  } catch (error) {
    console.log(error);
  }
};

// 유저 정보 가져오기
export const getUser = async (userId) => {
  try {
    const response = await api.get(`/users/${userId}`);
    return response.data;
  } catch (error) {
    console.log(error);
  }
};

// 유저 수정
export const modifyUser = async (userId, userData) => {
  try {
    const response = await api.patch(`/users/${userId}`, userData);
    return response.data;
  } catch (error) {
    console.log(error);
  }
};
