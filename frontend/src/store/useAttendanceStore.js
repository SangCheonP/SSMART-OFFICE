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
  persist(
    (set) => ({
      memberData: [],
      userTodoData: [],
      searchResults: [],

      setMemberData: (data) => set({ memberData: data }),
      setSelectedDate: (date) => set({ selectedDate: date }),
      setSearchResults: (data) => set({ searchResults: data }),

      // 전체 사용자 목록
      fetchUserList: async () => {
        try {
          const response = await fetchUserList();
          const allMembers = response.data.data.content;

          // 현재 로그인한 사용자 정보 가져오기
          const { employeeNumber } = useMyInfoStore.getState();

          // 현재 로그인한 사용자를 제외한 목록 필터링
          const filteredMembers = allMembers.filter(
            (member) => member.employeeNumber !== employeeNumber
          );

          // 필터링된 데이터 저장
          set({ memberData: filteredMembers });
          console.log(
            "로그인한 유저 제외한 사용자 목록 조회 성공:",
            filteredMembers
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
      // 사원 검색
      fetchFindUser: async (searchQuery) => {
        try {
          const response = await fetchFindUser(searchQuery);
          console.log("사원 검색 응답 데이터:", response.data.data.content);
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
          console.log("사원 좌석 조회 응답 데이터:", response.data.data);
          set({
            userSeatData: response.data.data || [], // 좌석 데이터를 store에 저장
          });
        } catch (error) {
          console.error("사원 좌석 데이터를 가져오는 중 오류 발생:", error);
          set({ userSeatData: [] });
        }
      },
    }),
    {
      name: "attendance-storage",
    }
  )
);
export default useAttendanceStore;
