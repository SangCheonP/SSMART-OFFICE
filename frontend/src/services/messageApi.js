import { Client } from "@stomp/stompjs";
import api from "./api";

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
      const response = await api.post(`/chats/chatroom/${userId}`, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
        },
      });
      console.log("API 응답 데이터:", response.data);
      // 응답 데이터 확인
      if (
        response.data &&
        response.data.data &&
        response.data.data.chatRoomId
      ) {
        return response.data.data.chatRoomId;
      } else {
        throw new Error("Invalid response format");
      }
    } catch (error) {
      console.error("채팅방 생성 실패:", error);
      throw error;
    }
  };

  // 메시지 조회
  const fetchMessages = async (chatRoomId) => {
    try {
      const response = await api.get(`/chats/messages/${chatRoomId}`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
        },
      });

      if (response.data && response.data.data) {
        return response.data.data;
      } else {
        throw new Error("Invalid response format");
      }
    } catch (error) {
      console.error("메시지 조회 실패:", error);
      throw error;
    }
  };

  // 전체 채팅방 조회(최신순)
  const fetchChatrooms = async () => {
    try {
      const response = await api.get(`/chats/chatroom`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
        },
      });
      if (response.data && response.data.data) {
        console.log("전체 채팅방 조회 성공:", response.data.data);
        return response.data.data; // 채팅방 데이터 반환
      } else {
        throw new Error("Invalid response format");
      }
    } catch (error) {
      console.error("채팅방 조회 실패:", error);
      throw error;
    }
  };

  return {
    sendMessage,
    subscribe,
    createChatRoom,
    fetchMessages,
    fetchChatrooms,
  };
})();

export default messageApi;
