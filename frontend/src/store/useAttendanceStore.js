import { create } from "zustand";
import { persist } from "zustand/middleware";
import api from "../services/api";

const useAttendanceStore = create(
  persist(
    (set) => ({
      memberData: [],
      userTodoData: {},

      setMemberData: (data) => set({ memberData: data }),

      // 전체 사용자 목록
      fetchUserList: async () => {
        try {
          const response = await api.get("/api/v1/users");
          set({ memberData: response.data.data.content });
          console.log(
            "전체 사용자 목록 조회 성공:",
            response.data.data.content
          );
        } catch (error) {
          console.error("사용자 데이터를 가져오는 중 오류 발생:", error);
        }
      },
      // 사원 일정 일별 조회
      fetchUserTodo: async (userId) => {
        try {
          const response = await api.get(
            `/api/v1/assignments/assignments/${userId}`
          );
          set({ userTodoData: response.data.data.assignments });
        } catch (error) {
          console.error("사원 일정 데이터를 가져오는 중 오류 발생:", error);
        }
      },
    }),
    {
      name: "attendance-storage",
    }
  )
);
export default useAttendanceStore;
