import api from "@/services/api";
import axios from "axios";
import useAuthStore from "@/store/useAuthStore";
import useMyInfoStore from "@/store/useMyInfoStore";
import { fetchMyInfo } from "@/services/myInfoAPI";
import { handleError } from "@/utils/errorHandler";
import { handleSuccess } from "@/utils/successHandler";

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

      await fetchMyInfo();
      handleSuccess("로그인 성공!");
      navigate("/", { replace: true });
    }
  } catch (e) {
    handleError(e);
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
  } catch (e) {
    handleError(e);
  }
};
