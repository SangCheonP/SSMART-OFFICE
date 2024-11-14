import axios from "axios";
import useAuthStore from "@/store/useAuthStore";
import Cookies from "js-cookie";

const SERVER_BASE_URL = import.meta.env.VITE_SERVER_BASE_URL;

// 요청을 보낼 기본 URL 설정
const api = axios.create({
  baseURL: SERVER_BASE_URL,
});

// 요청을 보내기 전 access token을 header에 담아 보내기 위한 코드
api.interceptors.request.use((config) => {
  const { accessToken } = useAuthStore.getState();

  if (accessToken) {
    config.headers["Authorization"] = `Bearer ${accessToken}`;
  }
  return config;
});

// refresh token으로 access token 재발급
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response.status === 401 && !originalRequest._retry) {
      // 401은 너무 큰 범위
      originalRequest._retry = true;
      const refreshToken = Cookies.get("refreshToken");
      if (refreshToken) {
        try {
          const { data } = await axios.post("/auth/token/refresh", {
            refreshToken,
          });
          useAuthStore.getState().setAuth(true, data.accessToken);
          return api(originalRequest);
        } catch (refreshError) {
          useAuthStore.getState().clearAuth();
          return Promise.reject(refreshError);
        }
      }
    }
    return Promise.reject(error);
  }
);

export default api;
