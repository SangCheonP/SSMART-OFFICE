import { useState } from "react";
import Icon from "@/assets/Login/LoginImage.svg?react";
import styles from "@/styles/Login/Login.module.css";
import GoogleLogin from "@/components/Login/GoogleLogin";

import { setLogin } from "@/services/authAPI";
import { useNavigate } from "react-router-dom";

const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      await setLogin(email, password, navigate);
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
