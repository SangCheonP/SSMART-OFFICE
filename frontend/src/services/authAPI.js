import api from "@/services/api";
import axios from "axios";
import useAuthStore from "@/store/useAuthStore";
import useMyInfoStore from "@/store/useMyInfoStore";
import { fetchMyInfo } from "@/services/myInfoAPI";

const BASE_URL = import.meta.env.VITE_SERVER_BASE_URL;

// 일반 로그인
export const setLogin = async (email, password, navigate) => {
  const setAuth = useAuthStore.getState().setAuth;
  try {
    const response = await axios.post(`${BASE_URL}/auth/login`, {
      email: email,
      password: password,
    });
    if (response.data.status === 200 || response.data.status === 201) {
      const accessToken = response.headers["authorization"];
      setAuth(accessToken);

      console.log("일반로그인");
      await fetchMyInfo();

      navigate("/", { replace: true });
    }
  } catch (error) {
    console.error("Login failed:", error);
  }
};

// 로그아웃
export const setLogout = async (clearAuth, navigate) => {
  const clearMyInfoData = useMyInfoStore.getState().clearMyInfoData;
  try {
    const response = await api.post("/auth/logout");
    if (response.data.status === 200 || response.data.status === 201) {
      clearAuth();
      clearMyInfoData();
      navigate("/login", { replace: true });
    }
  } catch (error) {
    console.error("Logout failed:", error);
  }
};
