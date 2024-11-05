import { Route, Routes } from "react-router-dom";

import Home from "./pages/Home";
import Seat from "./pages/Seat";
import MyPage from "./pages/MyPage";
import Message from "./pages/Message";
import SeatingByFloor from "./components/SeatingByFloor";
import Header from "./components/common/Header";

import styles from "./styles/AppRoutes.module.css";

const AppRoutes = () => {
  return (
    <div className={styles.appRoutes}>
      <Header />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/seat" element={<Seat />}>
          <Route index element={<SeatingByFloor floor="1F" />} />
          <Route path="1" element={<SeatingByFloor floor="1" />} />
          <Route path="2" element={<SeatingByFloor floor="2" />} />
          <Route path="3" element={<SeatingByFloor floor="3" />} />
          <Route path="4" element={<SeatingByFloor floor="4" />} />
        </Route>
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/message" element={<Message />} />
      </Routes>
    </div>
  );
};
export default AppRoutes;
