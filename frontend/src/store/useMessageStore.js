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
      const updatedMessages = [...state.messages, message];
      // console.log("[디버깅] 메시지 상태 업데이트: ", updatedMessages); // 상태 업데이트 확인
      return { messages: updatedMessages };
      // return { messages: [...state.messages, message] };
    }),

  // 채팅방 생성 및 WebSocket 구독
  createAndSubscribeToChatRoom: async (userId) => {
    try {
      if (!userId) {
        throw new Error("userId가 제공되지 않았습니다.");
      }

      // 채팅방 생성
      const chatRoomId = await messageApi.createChatRoom(userId);

      // 상태 업데이트
      set({ chatRoomId });

      // 메시지 조회 및 상태 설정
      const messages = await get().fetchAndSetMessages(chatRoomId);
      // console.log("초기 메시지 로드 완료:", messages);

      // WebSocket 구독 경로 정의
      const destination = `/api/v1/chats/ws/topic/${chatRoomId}`;
      // const destination = `/topic/chat/${chatRoomId}`;
      // console.log(`[디버깅] 구독할 채팅방 경로: ${destination}`); // 구독 경로 확인

      // WebSocket 구독 중복 방지
      const { subscribed } = get();
      if (!subscribed) {
        // console.log("[디버깅] 구독 시작:", destination);
        await messageApi.subscribe(destination, (message) => {
          // console.log("[디버깅] 구독 콜백 실행. 수신된 메시지:", message); // 수신된 메시지 확인
          set((state) => ({ messages: [...state.messages, message] }));
        });
        set({ subscribed: true });
        // console.log("WebSocket 구독 성공:", destination);
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

      await messageApi.sendMessage(destination, {
        type: "TEXT",
        content: messageContent,
      });

      // console.log("메시지 전송 성공:", messageContent);
    } catch (error) {
      console.error("메시지 전송 실패:", error);
    }
  },

  // 메시지 조회 및 상태 설정
  fetchAndSetMessages: async (chatRoomId) => {
    try {
      const messages = await messageApi.fetchMessages(chatRoomId);
      const currentUserId = get().userId || ""; // 현재 사용자 ID 가져오기

      // 메시지 배열에 isSender 필드를 추가
      const enrichedMessages = messages.map((message) => ({
        ...message,
        isSender: message.userId === currentUserId, // 메시지 보낸 사람 판단
      }));

      const currentMessages = get().messages;

      // 중복된 상태 업데이트 방지
      if (
        JSON.stringify(currentMessages) !== JSON.stringify(enrichedMessages)
      ) {
        set({ messages: enrichedMessages }); // 상태 업데이트
      }

      // console.log("메시지 로드 및 isSender 추가 완료:", enrichedMessages);
      return enrichedMessages;
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
      // console.log("채팅방 목록 업데이트 완료:", chatrooms);
    } catch (error) {
      console.error("채팅방 목록 업데이트 실패:", error);
    }
  },
}));

export default useMessageStore;
