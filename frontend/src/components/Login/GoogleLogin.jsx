import { useEffect } from "react";
import useAuthStore from "@/store/useAuthStore";
import { useLocation, useNavigate } from "react-router-dom";
import styles from "@/styles/Login/GoogleLogin.module.css";
import GoogleIcon from "@/assets/Login/GoogleIcon.svg?react";
import { fetchMyInfo } from "@/services/myInfoAPI";
import Swal from "sweetalert2";
import { handleSuccess } from "@/utils/successHandler";

const BASE_URL = import.meta.env.VITE_SERVER_BASE_URL;

const GoogleLogin = () => {
  const setAuth = useAuthStore((state) => state.setAuth);
  const location = useLocation();
  const navigate = useNavigate();

  const handleGoogleLogin = () => {
    window.location.href = `${BASE_URL}/auth/oauth2/authorization/google`;
  };

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const accessToken = queryParams.get("accessToken");
    const errorCode = queryParams.get("code");
    const errorMessage = queryParams.get("message");

    if (accessToken) {
      setAuth(accessToken);

      // 로그인을 성공했다면 유저 정보를 다시 요청
      fetchMyInfo();
      handleSuccess("로그인 성공!");
      navigate("/", { replace: true });
    } else if (errorCode && errorMessage) {
      console.log(errorCode, errorMessage);
      Swal.fire({
        icon: "error",
        title: "로그인 실패",
        text: decodeURIComponent(errorMessage),
        confirmButtonText: "확인",
      });

      // 필요 시 특정 페이지로 리디렉션
      navigate("/login", { replace: true });
    }
  }, [location.search, setAuth, navigate]);
  return (
    <div onClick={handleGoogleLogin} className={styles.button}>
      <GoogleIcon />
      Google로 로그인
    </div>
  );
};

export default GoogleLogin;
