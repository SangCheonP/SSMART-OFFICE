import { useEffect } from "react";
import useAuthStore from "@/store/useAuthStore";
import { useLocation, useNavigate } from "react-router-dom";
import styles from "@/styles/Login/GoogleLogin.module.css";
import GoogleIcon from "@/assets/Login/GoogleIcon.svg?react";

const GoogleLogin = () => {
  const setAuth = useAuthStore((state) => state.setAuth);
  const location = useLocation();
  const navigate = useNavigate();

  const handleGoogleLogin = () => {
    window.location.href =
      "https://k11b202.p.ssafy.io/oauth2/authorization/google";
  };

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const accessToken = queryParams.get("accessToken");

    if (accessToken) {
      setAuth(accessToken);
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
