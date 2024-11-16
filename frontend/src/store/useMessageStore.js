import { create } from "zustand";
import { persist } from "zustand/middleware";

const useHomeStore = create(
  persist((set) => ({
    // 채팅방 최신순 전체 조회
  }))
);
