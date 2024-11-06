import { useState } from "react";
import Icon from "./../assets/Login/LoginImage.svg?react";

import styles from "./../styles/Login/Login.module.css";

const Login = () => {
  const [email, setEmail] = useState();
  const [password, setPassword] = useState();
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [error, setError] = useState(null);

  return (
    <div className={styles.container}>
      <div className={styles.left}>
        <Icon className={styles.icon} />
      </div>
      <div className={styles.right}>
        <div>
          <h1 className={styles.title}>Log In</h1>
          <form>
            <input type="email" name="email" id="email" placeholder="Email" />
            <br />
            <input
              type="password"
              name="password"
              id="password"
              placeholder="Password"
            />
            <div className="buttonBox">
              <button type="submit" className={styles.button}>
                로그인
              </button>
              <button className={styles.googleButton}>구글 로그인</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};
export default Login;
