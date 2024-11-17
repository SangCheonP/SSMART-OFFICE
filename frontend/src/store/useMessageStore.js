import { create } from "zustand";
import messageApi from "@/services/messageApi";
import useAttendanceStore from "./useAttendanceStore";

const useMessageStore = create((set, get) => ({
  messages: [],
  chatRoomId: null, // 현재 채팅방 ID 상태

  // 메시지 추가
  addMessage: (message) =>
    set((state) => ({ messages: [...state.messages, message] })),

  // 채팅방 생성 및 WebSocket 구독
  createAndSubscribeToChatRoom: async (memberName) => {
    try {
      const { memberData } = useAttendanceStore.getState();

      const selectedMember = memberData.find(
        (member) => member.name === memberName
      );

      if (!selectedMember) {
        console.error(`Member not found. memberName: ${memberName}`);
        return;
      }

      const userId = selectedMember.userId;

      // 채팅방 생성
      const chatRoomId = await messageApi.createChatRoom(userId);
      console.log("생성된 채팅방 ID:", chatRoomId);

      // 상태 업데이트
      set({ chatRoomId });

      // 메시지 조회 및 상태 설정
      const messages = await get().fetchAndSetMessages(chatRoomId);
      console.log("초기 메시지:", messages);

      // WebSocket 구독 경로 정의
      const destination = `/topic/chat/${chatRoomId}`;
      console.log("구독할 경로:", destination);

      // WebSocket 구독
      await messageApi.subscribe(destination, (message) => {
        console.log("수신된 메시지:", message);
        set((state) => ({ messages: [...state.messages, message] }));
      });

      console.log("구독 성공:", destination);
    } catch (error) {
      console.error("채팅방 생성 및 구독 실패:", error);
    }
  },

  // 메시지 전송
  sendMessage: async (messageContent) => {
    try {
      const { chatRoomId, addMessage } = get();

      if (!chatRoomId) {
        throw new Error("현재 채팅방 ID가 설정되지 않았습니다.");
      }

      const destination = `/api/v1/chats/ws/app/${chatRoomId}`;
      console.log("메시지를 보낼 경로:", destination);
      console.log("전송할 메시지 내용:", messageContent);

      const message = {
        content: messageContent,
        createdTime: Date.now(),
        isSender: true,
        profileImageUrl: "default-profile.png",
      };

      await messageApi.sendMessage(destination, {
        type: "TEXT",
        content: messageContent,
      });

      console.log("메시지 전송 성공:", messageContent);
      addMessage(message);
    } catch (error) {
      console.error("메시지 전송 실패:", error);
    }
  },

  // 메시지 조회 및 상태 설정
  fetchAndSetMessages: async (chatRoomId) => {
    try {
      const messages = await messageApi.fetchMessages(chatRoomId);
      set({ messages });
      return messages;
    } catch (error) {
      console.error("메시지 상태 업데이트 실패:", error);
      return [];
    }
  },
}));

export default useMessageStore;
