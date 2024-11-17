import { Client } from "@stomp/stompjs";

const messageApi = (() => {
  const accessToken =
    JSON.parse(localStorage.getItem("auth"))?.state?.accessToken || "";

  const client = new Client({
    brokerURL: "ws://k11b202.p.ssafy.io:8080/api/v1/chats/ws",
    connectHeaders: {
      Authorization: accessToken,
    },
    reconnectDelay: 5000,
    debug: (str) => console.log(str),
  });

  client.activate();

  // 메시지 전송 (연결 상태 확인 후 전송)
  const sendMessage = async (destination, body) => {
    if (!client.connected) {
      await new Promise((resolve) => {
        client.onConnect = resolve;
      });
    }
    client.publish({
      destination,
      body: JSON.stringify(body),
    });
  };

  // 구독 (연결 상태 확인 후 구독)
  const subscribe = async (destination, callback) => {
    if (!client.connected) {
      await new Promise((resolve) => {
        client.onConnect = resolve;
      });
    }
    client.subscribe(destination, (message) => {
      const parsedMessage = JSON.parse(message.body);
      callback(parsedMessage);
    });
  };

  // 채팅방 생성
  const createChatRoom = async (userId) => {
    try {
      const response = await fetch(`/api/v1/chats/chatroom/${userId}`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
        },
      });

      if (!response.ok) {
        throw new Error("Failed to create chat room");
      }

      const data = await response.json();
      console.log("채팅방 생성 성공, ID:", data.data.chatRoomId);
      return data.data.chatRoomId;
    } catch (error) {
      console.error("채팅방 생성 실패:", error);
      throw error;
    }
  };

  return { sendMessage, subscribe, createChatRoom };
})();

export default messageApi;
