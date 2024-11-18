import { create } from "zustand";
import { persist } from "zustand/middleware";
import api from "@/services/api";
import {
  fetchUserTodo,
  fetchUserList,
  fetchFindUser,
  fetchUserSeats,
} from "@/services/attendanceApi";
import useMyInfoStore from "./useMyInfoStore";

const useAttendanceStore = create(
  (set) => ({
    memberData: [],
    userTodoData: [],
    searchResults: [],
    memberSeats: {},

    clearSearchResults: () => set({ searchResults: [] }),
    setMemberData: (data) => set({ memberData: data }),
    setSelectedDate: (date) => set({ selectedDate: date }),
    setSearchResults: (data) => set({ searchResults: data }),
    setMemberSeats: (userId, seatData) =>
      set((state) => ({
        memberSeats: { ...state.memberSeats, [userId]: seatData },
      })),

    // 전체 사용자 목록
    fetchUserList: async () => {
      try {
        const response = await fetchUserList();
        const allMembers = response.data.data.content;

        // 현재 로그인한 사용자 정보 가져오기
        const { userId } = useMyInfoStore.getState();
        console.log("유저아이디", userId);

        // 현재 로그인한 사용자를 제외한 목록 필터링
        const filteredMembers = allMembers.filter(
          (member) => member.userId !== userId
        );

        // 필터링된 데이터 저장
        set({ memberData: filteredMembers });
      } catch (error) {
        console.error("사용자 데이터를 가져오는 중 오류 발생:", error);
      }
    },

    // 사원 일정 일별 조회
    fetchUserTodo: async (userId, month, day) => {
      try {
        const response = await fetchUserTodo(userId, month, day);
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
    // 사원 검색
    fetchFindUser: async (searchQuery) => {
      try {
        const response = await fetchFindUser(searchQuery);
        set({
          searchResults: Array.isArray(response.data.data.content)
            ? response.data.data.content
            : [],
        });
      } catch (error) {
        console.error("사원 검색 데이터를 가져오는 중 오류 발생:", error);
        set({ searchResults: [] });
      }
    },
    // 사원 좌석 조회
    fetchUserSeats: async (userId) => {
      try {
        const response = await fetchUserSeats(userId);
        set((state) => ({
          memberSeats: {
            ...state.memberSeats,
            [userId]: response.data.data || "좌석 없음",
          },
        }));
      } catch (error) {
        console.error("사원 좌석 데이터를 가져오는 중 오류 발생:", error);

        // 오류 시 좌석 없음 처리
        set((state) => ({
          memberSeats: { ...state.memberSeats, [userId]: "좌석 없음" },
        }));
      }
    },
  }),
  {
    name: "attendance-storage",
  }
);
export default useAttendanceStore;
