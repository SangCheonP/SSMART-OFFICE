import Menu from "./components/Menu";
import AppRoutes from "./AppRoutes.jsx";
// import Login from "./pages/Login.jsx";

import styles from "./styles/App.module.css";
import "./styles/Reset.css";

const App = () => {
  return (
    <div className={styles.container}>
      {/* <Login /> */}
      <div className={styles.menu}>
        <Menu />
      </div>
      <div className={styles.appRoutes}>
        <AppRoutes />
      </div>
    </div>
  );
};

export default App;
