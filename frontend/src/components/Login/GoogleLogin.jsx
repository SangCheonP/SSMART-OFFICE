import { useEffect } from "react";
import useAuthStore from "@/store/authStore";
import { useLocation, useNavigate } from "react-router-dom";

const GoogleLogin = () => {
  const setAuth = useAuthStore((state) => state.setAuth);
  const location = useLocation();
  const navigate = useNavigate();

  const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8081/oauth2/authorization/google";
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
    <div>
      <button onClick={handleGoogleLogin}>Google로 로그인</button>
    </div>
  );
};

export default GoogleLogin;
