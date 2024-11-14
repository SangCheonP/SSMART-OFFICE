import { Routes, Route } from "react-router-dom";

import Menu from "@/components/Menu";
import Header from "@/components/common/Header";
import PrivateRoute from "@/components/PrivateRoute";

import Home from "@/pages/Home";
import Seat from "@/pages/Seat";
import MyPage from "@/pages/MyPage";
import Message from "@/pages/Message";
import Attendance from "@/pages/Attendance";
import SeatingByFloor from "@/components/Seat/SeatingByFloor";
import styles from "@/styles/App.module.css";

const AppRoutes = () => {
  return (
    <div className={styles.container}>
      <div className={styles.menu}>
        <Menu />
      </div>
      <div className={styles.appRoutes}>
        <Header />
        <Routes>
          <Route element={<PrivateRoute />}>
            <Route path="/" element={<Home />} />
            <Route path="/seat" element={<Seat />}>
              <Route path=":floor" element={<SeatingByFloor />} />
            </Route>
            <Route path="/mypage" element={<MyPage />} />
            <Route path="/message" element={<Message />} />
            <Route path="/attendance" element={<Attendance />} />
          </Route>
        </Routes>
      </div>
    </div>
  );
};

export default AppRoutes;
