import { create } from "zustand";
import messageApi from "@/services/messageApi";
import useAttendanceStore from "./useAttendanceStore";

const useMessageStore = create((set) => ({
  messages: [],
  addMessage: (message) =>
    set((state) => ({ messages: [...state.messages, message] })),

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
      console.log(selectedMember);

      const userId = selectedMember.userId;
      console.log("유저아이디:", selectedMember.userId);

      // 채팅방 생성 API 호출 (messageApi 활용)
      const chatRoomId = await messageApi.createChatRoom(userId);

      // 생성된 채팅방 ID로 구독
      console.log(chatRoomId);
      messageApi.subscribe(destination, (message) => {
        set((state) => ({ messages: [...state.messages, message] }));
      });
    } catch (error) {
      console.error("채팅방 생성 및 구독 실패:", error);
    }
  },
  sendMessage: (destination, messageContent) => {
    messageApi.sendMessage(destination, {
      type: "TEXT",
      content: messageContent,
    });
  },
}));

export default useMessageStore;
