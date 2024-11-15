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
      calendarData: { data: { attendances: [] } },
      todoData: null,
      attendanceData: null,

      // 캘린더 데이터 월별 조회
      fetchCalendarData: async (month) => {
        try {
          const response = await fetchCalendarData(month);
          set({ calendarData: response.data });
        } catch (error) {
          console.error("캘린더 데이터 월별 조회 오류", error);
        }
      },

      // 캘린더 일정 일별 조회
      fetchTodoData: async (month, day) => {
        try {
          const response = await fetchTodoData(month, day);
          console.log("캘린더 일별 조회:", response.data);
          set({ todoData: response.data });
        } catch (error) {
          console.error("캘린더 일정 일별 조회 오류", error);
        }
      },

      // 출퇴근 정보 조회
      fetchAttendanceData: async (month, day) => {
        try {
          const response = await fetchAttendanceData(month, day);
          console.log("출퇴근 조회:", response.data);
          set({ attendanceData: response.data });
        } catch (error) {
          console.error("출퇴근 정보 조회 오류", error);
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
          set((state) => ({
            calendarData: {
              ...state.calendarData,
              data: {
                ...state.calendarData.data,
                attendances: [
                  ...(state.calendarData.data.attendances || []),
                  response.data,
                ],
              },
            },
          }));
          return response.data;
        } catch (error) {
          console.error("일정 추가 실패 store:", error);
          throw error;
        }
      },
    }),
    {
      name: "home-storage",
      getStorage: () => localStorage,
    }
  )
);

export default useHomeStore;
