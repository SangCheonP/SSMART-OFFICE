import AppRoutes from "@/AppRoutes.jsx";
import Login from "@/pages/Login.jsx";
import "react-big-calendar/lib/css/react-big-calendar.css";
import "@/styles/Reset.css";

import useAuthStore from "@/store/authStore";
import { BrowserRouter, Route, Routes } from "react-router-dom";

const App = () => {
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);

  return (
    <BrowserRouter>
      <Routes>
        {!isAuthenticated ? (
          // 로그인하지 않은 경우 Login 페이지로 이동
          <Route path="/*" element={<Login />} />
        ) : (
          // 로그인한 경우 AppRoutes로 이동
          <Route path="/*" element={<AppRoutes />} />
        )}
      </Routes>
    </BrowserRouter>
  );
};

export default App;
