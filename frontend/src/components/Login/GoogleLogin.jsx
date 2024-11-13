import { useEffect } from "react";
import useAuthStore from "@/store/useAuthStore";
import { useLocation, useNavigate } from "react-router-dom";
import styles from "@/styles/Login/GoogleLogin.module.css";
import GoogleIcon from "@/assets/Login/GoogleIcon.svg?react";
import api from "../../services/api";

const GOOGLE_LOGIN_URL = import.meta.env.VITE_GOOGLE_LOGIN_URL;
const BASE_URL = import.meta.env.VITE_SERVER_BASE_URL;

const GoogleLogin = () => {
  const setAuth = useAuthStore((state) => state.setAuth);
  const location = useLocation();
  const navigate = useNavigate();

  const handleGoogleLogin = () => {
    window.location.href = GOOGLE_LOGIN_URL;
  };

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const accessToken = queryParams.get("accessToken");

    if (accessToken) {
      setAuth(accessToken);

      // 로그인을 성공했다면 유저 정보를 다시 요청
      const userInformation = api.get(`${BASE_URL}/users/me`);
      const userData = userInformation.data.data;
      console.log(userData);

      navigate("/");
    }
  }, [location.search, setAuth, navigate]);
  return (
    <button onClick={handleGoogleLogin} className={styles.button}>
      <GoogleIcon />
      Google로 로그인
    </button>
  );
};

export default GoogleLogin;
