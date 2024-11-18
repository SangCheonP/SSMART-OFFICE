import { create } from "zustand";
import { persist } from "zustand/middleware";
import {
  fetchCalendarData,
  fetchTodoData,
  fetchAttendanceData,
  addCalendarEvent,
  checkEvent,
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
          // 날짜 형식 변환: YYYY-MM-DD (월과 일이 항상 두 자리로 표시)
          const formatDate = (date) => {
            const d = new Date(date);
            const year = d.getFullYear();
            const month = String(d.getMonth() + 1).padStart(2, "0"); // 월을 두 자리로
            const day = String(d.getDate()).padStart(2, "0"); // 일을 두 자리로
            return `${year}-${month}-${day}`;
          };

          const formattedDate = formatDate(assignmentDate);

          // 서버로 데이터 전송
          const response = await addCalendarEvent(
            assignmentName,
            formattedDate, // 포맷된 날짜를 사용
            assignmentType,
            description
          );

          // 일정 추가 후 데이터 갱신
          const addedMonth =
            new Date(formattedDate).getFullYear() * 100 +
            (new Date(formattedDate).getMonth() + 1);
          const updatedResponse = await fetchCalendarData(addedMonth);

          // 상태 업데이트: 월별 데이터 갱신
          set({ calendarData: updatedResponse.data });

          // 일정이 추가된 날짜 데이터 갱신
          const addedDay = new Date(formattedDate).getDate();
          const updatedDayResponse = await fetchTodoData(addedMonth, addedDay);

          // 상태 업데이트: 일별 데이터 갱신
          set({ todoData: updatedDayResponse?.data || [] });

          return response.data;
        } catch (error) {
          console.error("일정 추가 실패 store:", error);
          throw error;
        }
      },

      // 일정 완료
      checkEvent: async (assignmentId, assignmentDate) => {
        try {
          // isNaN으로 오는 에러 처리
          if (!assignmentDate || isNaN(new Date(assignmentDate))) {
            throw new Error("유효하지 않은 날짜 값입니다.");
          }
          // 일정 완료 API 호출
          const response = await checkEvent(assignmentId);

          // 일정이 완료된 날짜 데이터 갱신
          const updatedMonth =
            new Date(assignmentDate).getFullYear() * 100 +
            (new Date(assignmentDate).getMonth() + 1);
          const updatedDay = new Date(assignmentDate).getDate();

          const updatedDayResponse = await fetchTodoData(
            updatedMonth,
            updatedDay
          );
          console.log("fetchTodoData 응답:", updatedDayResponse?.data);
          // 상태 업데이트: 일별 데이터 갱신
          set({ todoData: updatedDayResponse?.data || [] });

          return response.data;
        } catch (error) {
          console.error("일정 완료 처리 실패:", error);
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
