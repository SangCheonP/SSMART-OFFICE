import { create } from "zustand";
import { persist } from "zustand/middleware";
import Cookies from "js-cookie";

const useAuthStore = create(
  persist(
    (set) => ({
      isAuthenticated: false,
      accessToken: null,
      setAuth: (accessToken) => {
        set({
          isAuthenticated: true,
          accessToken,
        });
      },
      clearAuth: () => {
        set({
          isAuthenticated: false,
          accessToken: null,
        });
        Cookies.remove("refresh");
      },
    }),
    {
      name: "auth",
      partialize: (state) => ({
        isAuthenticated: state.isAuthenticated,
        accessToken: state.accessToken,
      }),
      getStorage: () => sessionStorage,
    }
  )
);

export default useAuthStore;
