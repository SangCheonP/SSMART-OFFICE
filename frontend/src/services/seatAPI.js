import api from "@/services/api";
import { handleError } from "@/utils/errorHandler";

// 좌석 조회
export const fetchSeats = async (floor) => {
  try {
    const response = await api.get(`/seats?floor=${floor}`);
    return response.data.data;
  } catch (error) {
    handleError(error);
  }
};
