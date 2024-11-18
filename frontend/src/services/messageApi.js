import { Client } from "@stomp/stompjs";
import api from "./api";

const messageApi = (() => {
  let client;

  const getAccessToken = () => {
    const authData = localStorage.getItem("auth");
    if (!authData) {
      console.error(
        "Authorization 토큰 없음: localStorage에 'auth' 데이터가 없습니다."
      );
      return null;
    }

    try {
      const accessToken = JSON.parse(authData)?.state?.accessToken || null;
      if (!accessToken) {
        console.error(
          "Authorization 토큰 없음: 'auth.state.accessToken'가 비어 있습니다."
        );
      }
      return accessToken;
    } catch (error) {
      console.error("Authorization 토큰 파싱 에러:", error);
      return null;
    }
  };

  const initializeStompClient = () => {
    const accessToken = getAccessToken();
    if (!accessToken) {
      console.error("STOMP 클라이언트 초기화 실패: 유효하지 않은 토큰.");
      return null;
    }

    client = new Client({
      brokerURL: "ws://k11b202.p.ssafy.io:8080/api/v1/chats/ws",
      connectHeaders: {
        Authorization: accessToken,
      },
      reconnectDelay: 5000,
      // debug: (str) => console.log(`[STOMP DEBUG]: ${str}`), // 디버깅 로그
      onStompError: (frame) => {
        console.log("[STOMP DEBUG] STOMP 에러 발생:", frame);
      },
    });

    // STOMP 연결 이벤트 핸들러
    client.onConnect = (frame) => {
      // console.log("[STOMP DEBUG] 연결 성공. 세부 정보:", frame); // 연결 성공 세부 정보
    };

    // STOMP 연결 종료 이벤트 핸들러
    client.onDisconnect = () => {
      console.warn("[STOMP DEBUG] WebSocket 연결이 종료되었습니다.");
    };

    // STOMP 연결 에러 이벤트 핸들러
    client.onStompError = (error) => {
      console.error("[STOMP DEBUG] STOMP 연결 에러 발생:", error.headers.message);
      console.error("[STOMP DEBUG] STOMP 에러 세부 정보:", error.body);
    }; 

    return client;
  };

  let checkInterval = null; // Interval 중복 방지용 변수

  const activateStomp = () => {
    if (!client) {
      // console.log("STOMP 클라이언트를 초기화합니다.");
      initializeStompClient();
    }

    if (!client.active) {
      // console.log("STOMP 클라이언트 활성화");
      client.activate();
    } else {
      // console.log("STOMP 클라이언트 이미 활성화됨");
    }

    // 연결 상태를 주기적으로 체크
    // if (!checkInterval) {
    //   console.log("WebSocket 연결 상태 확인을 시작합니다.");
    //   checkInterval = setInterval(checkConnectionStatus, 5000);
    // }
  };

  // const checkConnectionStatus = () => {
  //   if (client && client.connected) {
  //     console.log("[STOMP DEBUG] WebSocket 연결 상태: 연결됨");
  //   } else {
  //     console.warn("[STOMP DEBUG] WebSocket 연결 상태: 연결되지 않음");
  //   }
  // };

  const ensureConnected = () => {
    if (client && client.connected) {
      // console.log("[STOMP 연결 확인: 연결되어 있습니다]");
      return Promise.resolve();
    }

    if (!client || !client.active) {
      console.warn("[STOMP 연결 확인: 활성화되지 않음, 활성화 시도]");
      activateStomp();
    }

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
    // console.log(
    //   "[디버깅] 메시지 전송: Destination:",
    //   destination,
    //   "Body:",
    //   body
    // );

    try {
      client.publish({
        destination,
        body: JSON.stringify(body),
      });
      // console.log("[디버깅] 메시지 전송 성공");
    } catch (error) {
      console.error("[디버깅] 메시지 전송 실패:", error);
    }
  };

  // 구독 (연결 상태 확인 후 구독)
  const subscribe = async (destination, callback) => {
    await ensureConnected();
    // console.log(`[디버깅] 구독 경로: ${destination}`); // 구독 경로 확인
    client.subscribe(destination, (message) => {
      // console.log("[디버깅] 수신된 메시지:", message.body); // 수신된 메시지 확인
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
          Authorization: `Bearer ${getAccessToken()}`,
        },
      });

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
          Authorization: `Bearer ${getAccessToken()}`,
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
          Authorization: `Bearer ${getAccessToken()}`,
        },
      });

      if (response.data && response.data.data) {
        // console.log("전체 채팅방 조회 성공:", response.data.data);
        return response.data.data;
      } else {
        throw new Error("Invalid response format");
      }
    } catch (error) {
      console.error("채팅방 조회 실패:", error);
      throw error;
    }
  };

  return {
    ensureConnected,
    activateStomp,
    sendMessage,
    subscribe,
    createChatRoom,
    fetchMessages,
    fetchChatrooms,
  };
})();

export default messageApi;
