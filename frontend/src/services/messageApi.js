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

  // STOMP 연결 이벤트 핸들러
  client.onConnect = () => {
    console.log("STOMP 연결 성공");
  };

  // STOMP 연결 종료 이벤트 핸들러
  client.onDisconnect = () => {
    console.warn("STOMP 연결이 종료되었습니다.");
    // 필요 시 재연결 로직을 추가할 수 있음
  };

  // STOMP 연결 에러 이벤트 핸들러
  client.onStompError = (error) => {
    console.error("STOMP 연결 에러 발생:", error.headers.message);
    console.error("STOMP 에러 세부 정보:", error.body);
    // 에러 발생 시 연결을 재시도하거나 사용자에게 알림
  };

  client.activate();

  const ensureConnected = () => {
    if (client.connected) return Promise.resolve();

    return new Promise((resolve, reject) => {
      client.onConnect = resolve;
      client.onStompError = (error) => {
        console.error("STOMP 연결 오류:", error);
        reject(error);
      };
    });
  };

  // 메시지 전송 (연결 상태 확인 후 전송)
  const sendMessage = async (destination, body) => {
    await ensureConnected();
    client.publish({
      destination,
      body: JSON.stringify(body),
    });
  };

  // 구독 (연결 상태 확인 후 구독)
  const subscribe = async (destination, callback) => {
    await ensureConnected();
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
