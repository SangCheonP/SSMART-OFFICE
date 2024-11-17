import { create } from "zustand";
import messageApi from "@/services/messageApi";
import useAttendanceStore from "./useAttendanceStore";

const useMessageStore = create((set, get) => ({
  messages: [],
  chatrooms: [],
  chatRoomId: null,
  subscribed: false,

  // 메시지 추가
  addMessage: (message) =>
    set((state) => {
      return { messages: [...state.messages, message] };
    }),

  // 채팅방 생성 및 WebSocket 구독
  createAndSubscribeToChatRoom: async (userId) => {
    try {
      if (!userId) {
        throw new Error("userId가 제공되지 않았습니다.");
      }

      // 채팅방 생성
      const chatRoomId = await messageApi.createChatRoom(userId);
      console.log("생성된 채팅방 ID:", chatRoomId);

      // 상태 업데이트
      set({ chatRoomId });

      // 메시지 조회 및 상태 설정
      const messages = await get().fetchAndSetMessages(chatRoomId);
      console.log("초기 메시지 로드 완료:", messages);

      // WebSocket 구독 경로 정의
      const destination = `/topic/chat/${chatRoomId}`;
      console.log("구독할 경로:", destination);

      // WebSocket 구독 중복 방지
      const { subscribed } = get();
      if (!subscribed) {
        await messageApi.subscribe(destination, (message) => {
          console.log("수신된 메시지:", message);
          set((state) => ({ messages: [...state.messages, message] }));
        });
        set({ subscribed: true });
        console.log("WebSocket 구독 성공:", destination);
      }
    } catch (error) {
      console.error("채팅방 생성 및 구독 실패:", error);
    }
  },

  // 메시지 전송
  sendMessage: async (messageContent) => {
    try {
      const { chatRoomId } = get();

      if (!chatRoomId) {
        throw new Error("현재 채팅방 ID가 설정되지 않았습니다.");
      }

      const destination = `/api/v1/chats/ws/app/${chatRoomId}`;
      console.log("메시지를 보낼 경로:", destination);
      console.log("전송할 메시지 내용:", messageContent);

      await messageApi.sendMessage(destination, {
        type: "TEXT",
        content: messageContent,
      });

      console.log("메시지 전송 성공:", messageContent);
    } catch (error) {
      console.error("메시지 전송 실패:", error);
    }
  },

  // 메시지 조회 및 상태 설정
  fetchAndSetMessages: async (chatRoomId) => {
    try {
      const messages = await messageApi.fetchMessages(chatRoomId);
      const currentMessages = get().messages;

      // 중복된 상태 업데이트 방지
      if (JSON.stringify(currentMessages) !== JSON.stringify(messages)) {
        set({ messages });
      }

      return messages;
    } catch (error) {
      console.error("메시지 상태 업데이트 실패:", error);
      return [];
    }
  },

  // 채팅방 목록 가져오기
  fetchAndSetChatrooms: async () => {
    try {
      const chatrooms = await messageApi.fetchChatrooms();
      set({ chatrooms });
      console.log("채팅방 목록 업데이트 완료:", chatrooms);
    } catch (error) {
      console.error("채팅방 목록 업데이트 실패:", error);
    }
  },
}));

export default useMessageStore;
