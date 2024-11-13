import { useState } from "react";
import Icon from "@/assets/Login/LoginImage.svg?react";
import useAuthStore from "@/store/useAuthStore";

import styles from "@/styles/Login/Login.module.css";
import GoogleLogin from "@/components/Login/GoogleLogin";

import axios from "axios";
import api from "@/services/api";

const BASE_URL = import.meta.env.VITE_SERVER_BASE_URL;

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const setAuth = useAuthStore((state) => state.setAuth);

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post(`${BASE_URL}/auth/login`, {
        email: email,
        password: password,
      });

      const accessToken = response.headers["authorization"];
      setAuth(true, accessToken);

      // 로그인을 성공했다면 유저 정보를 다시 요청
      const userInformation = await api.get(`${BASE_URL}/users/me`);
      const userData = userInformation.data.data;
      console.log(userData);
    } catch (error) {
      console.log("로그인 실패 : " + error);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.left}>
        <Icon className={styles.icon} />
      </div>
      <div className={styles.right}>
        <div className={styles.title}>Log In</div>
        <form onSubmit={handleLogin}>
          <input
            type="email"
            name="email"
            id="email"
            placeholder="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <br />
          <input
            type="password"
            name="password"
            id="password"
            placeholder="Password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <div className={styles.buttonBox}>
            <button type="submit" className={styles.button}>
              로그인
            </button>
            <GoogleLogin />
          </div>
        </form>
      </div>
    </div>
  );
};
export default Login;
