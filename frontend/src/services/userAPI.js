import api from "@/services/api";
// 유저 등록
export const registerUser = async (userData) => {
  try {
    const response = await api.post("/users", userData);
    console.log("유저 API : ", response.data);
    return response.data;
  } catch (error) {
    console.log(error);
  }
};
