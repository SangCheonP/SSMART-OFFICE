import { create } from "zustand";
import { persist } from "zustand/middleware";

const useMyInfoStore = create(
  persist(
    (set) => ({
      employeeNumber: null,
      name: null,
      email: null,
      duty: null,
      position: null,
      profileImageUrl: null,
      phoneNumber: null,

      // 초기 데이터 설정
      setMyInfoData: (data) =>
        set({
          employeeNumber: data.employeeNumber,
          name: data.name,
          email: data.email,
          duty: data.duty,
          position: data.position,
          profileImageUrl: data.profileImageUrl,
          phoneNumber: data.phoneNumber,
        }),

      // 전화번호 업데이트 함수
      updatePhoneNumber: (phoneNumber) =>
        set(() => ({ phoneNumber: phoneNumber })),

      // 프로필 이미지 업데이트 함수
      updateProfileImage: (newUrl) => set(() => ({ profileImageUrl: newUrl })),
    }),
    {
      name: "my-info",
      partialize: (state) => ({
        employeeNumber: state.employeeNumber,
        name: state.name,
        email: state.email,
        duty: state.duty,
        position: state.position,
        profileImageUrl: state.profileImageUrl,
        phoneNumber: state.phoneNumber,
      }),
    }
  )
);

export default useMyInfoStore;
