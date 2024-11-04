import { Route, Routes } from "react-router-dom";

import Home from "./pages/Home";
import Seat from "./pages/Seat";
import MyPage from "./pages/MyPage";
import Message from "./pages/Message";
import Floor from "./pages/Floor";

import styles from "./styles/AppRoutes.module.css";

const AppRoutes = () => {
  return (
    <div className={styles.appRoutes}>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/seat" element={<Seat />}>
          <Route index element={<Floor />} />
        </Route>
        <Route path="/mypage" element={<MyPage />} />
        <Route path="/message" element={<Message />} />
      </Routes>
    </div>
  );
};
export default AppRoutes;
