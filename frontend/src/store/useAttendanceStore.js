import { create } from "zustand";
import { persist } from "zustand/middleware";
import api from "@/services/api";
import { fetchUserTodo, fetchUserList } from "@/services/attendanceApi";

const useAttendanceStore = create(
  persist(
    (set) => ({
      memberData: [],
      userTodoData: [],

      setMemberData: (data) => set({ memberData: data }),
      setSelectedDate: (date) => set({ selectedDate: date }),

      // 전체 사용자 목록
      fetchUserList: async () => {
        try {
          const response = await fetchUserList();
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
      fetchUserTodo: async (userId, month, day) => {
        try {
          const response = await fetchUserTodo(userId, month, day);
          console.log("fetchUserTodo 응답 데이터:", response.data);
          set({
            userTodoData: Array.isArray(response.data.data)
              ? response.data.data
              : [],
          });
        } catch (error) {
          console.error("사원 일정 데이터를 가져오는 중 오류 발생:", error);
          set({ userTodoData: [] });
        }
      },
    }),
    {
      name: "attendance-storage",
    }
  )
);
export default useAttendanceStore;
