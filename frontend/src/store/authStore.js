import { create } from "zustand";
import { persist } from "zustand/middleware";
import Cookies from "js-cookie";

const useAuthStore = create(
  persist((set) => ({
    isAuthenticated: false,
    accessToken: null,
    setAuth: (accessToken) => {
      set({
        isAuthenticated: true,
        accessToken,
      });
      console.log(accessToken);
    },
    clearAuth: () => {
      set({
        isAuthenticated: false,
        accessToken: null,
      });
      Cookies.remove("refresh");
      console.log(Cookies.get("refresh"));
    },
  }))
);

export default useAuthStore;
