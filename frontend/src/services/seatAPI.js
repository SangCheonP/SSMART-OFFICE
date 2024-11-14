import api from "@/services/api";

// 좌석 조회
export const fetchSeats = async (floor) => {
  try {
    const response = await api.get(`/seats?floor=${floor}`);
    return response.data.data;
  } catch (e) {
    throw e.response ? e.response.data : new Error("좌석 조회 실패");
  }
};
