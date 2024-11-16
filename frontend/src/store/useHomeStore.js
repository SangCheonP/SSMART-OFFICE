import { create } from "zustand";
import { persist } from "zustand/middleware";
import {
  fetchCalendarData,
  fetchTodoData,
  fetchAttendanceData,
  addCalendarEvent,
} from "@/services/homeApi";

const useHomeStore = create(
  persist(
    (set) => ({
      calendarData: { data: [] },
      todoData: [],
      attendanceData: [],

      // 캘린더 데이터 초기화
      resetCalendarData: () =>
        set({
          calendarData: { data: [] },
          todoData: null,
          attendanceData: null,
        }),

      // 캘린더 데이터 설정
      setCalendarData: (data) => set({ calendarData: data }),

      // 일정 데이터 설정
      setTodoData: (data) => set({ todoData: data }),

      // 출퇴근 데이터 설정
      setAttendanceData: (data) => set({ attendanceData: data }),

      // 캘린더 데이터 월별 조회
      fetchCalendarData: async (month) => {
        try {
          const response = await fetchCalendarData(month);
          set({ calendarData: response?.data || { data: [] } });
        } catch (error) {
          console.error("캘린더 데이터 월별 조회 오류", error);
        }
      },

      // 캘린더 일정 일별 조회
      fetchTodoData: async (month, day) => {
        try {
          const response = await fetchTodoData(month, day);
          console.log("캘린더 일별 조회:", response.data);
          set({ todoData: response?.data || [] });
        } catch (error) {
          console.error("캘린더 일정 일별 조회 오류", error);
          set({ todoData: [] });
        }
      },

      // 출퇴근 정보 조회
      fetchAttendanceData: async (month, day) => {
        try {
          const response = await fetchAttendanceData(month, day);
          console.log("출퇴근 조회:", response.data);
          set({ attendanceData: response?.data || [] });
        } catch (error) {
          console.error("출퇴근 정보 조회 오류", error);
          set({ attendanceData: [] });
        }
      },

      // 일정 추가
      addCalendarEvent: async (
        assignmentName,
        assignmentDate,
        assignmentType,
        description
      ) => {
        try {
          const response = await addCalendarEvent(
            assignmentName,
            assignmentDate,
            assignmentType,
            description
          );

          // 일정 추가 후 데이터 갱신
          const addedMonth =
            new Date(assignmentDate).getFullYear() * 100 +
            (new Date(assignmentDate).getMonth() + 1);
          const updatedResponse = await fetchCalendarData(addedMonth);

          // 상태 업데이트: 월별 데이터 갱신
          set({ calendarData: updatedResponse.data });

          return response.data;
        } catch (error) {
          console.error("일정 추가 실패 store:", error);
          throw error;
        }
      },
    }),
    {
      name: "home-storage",
      partialize: (state) => ({
        calendarData: state.calendarData,
        todoData: state.todoData,
        attendanceData: state.attendanceData,
      }),
      getStorage: () => localStorage,
    }
  )
);

export default useHomeStore;
