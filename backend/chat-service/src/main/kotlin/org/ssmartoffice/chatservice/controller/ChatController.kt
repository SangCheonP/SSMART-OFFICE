package org.ssmartoffice.chatservice.controller

import feign.Response
import org.springframework.http.ResponseEntity
import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ssmartoffice.chatservice.controller.port.ChatService
import org.ssmartoffice.chatservice.controller.request.MessageRequest
import org.ssmartoffice.chatservice.controller.response.CreateChatRoomResponse
import org.ssmartoffice.chatservice.controller.response.GetChatRoomResponse
import org.ssmartoffice.chatservice.controller.response.MessageResponse
import org.ssmartoffice.chatservice.domain.Message
import org.ssmartoffice.chatservice.global.dto.CommonResponse
import org.ssmartoffice.chatservice.service.logger


@RestController
@RequestMapping("/api/v1/chats")
class ChatController(
    private val sendingOperations: SimpMessageSendingOperations,
    val chatService: ChatService
) {


    //채팅 메세지 보내기
    @MessageMapping("/{roomId}")
    fun enter(messageRequest: MessageRequest, authentication: Authentication, @DestinationVariable roomId: Long) {
        val userId: Long = authentication.principal as Long
        logger.info { "채팅 메시지 전송 시작. 사용자 ID: $userId, 채팅방 ID: $roomId, 메시지 내용: ${messageRequest.content}" }

        val userChatroom = chatService.findUserChatroom(userId, roomId)
        if (userChatroom == null) {
            logger.error { "채팅방이 존재하지 않음 - roomId: $roomId, userId: $userId" }
            throw Exception("채팅방이 존재하지 않습니다.")
        }

        val message = Message(
            chatroom = userChatroom,
            userId = userId,
            content = messageRequest.content,
            type = messageRequest.type,
        )

        chatService.saveMessage(message)
        logger.info { "메시지 저장 완료 - content: ${message.content}, type: ${message.type}" }

        sendingOperations.convertAndSend("/api/v1/chats/ws/topic/$roomId", message)
        logger.info { "메시지 발행 완료 - destination: /api/v1/chats/ws/topic/$roomId" }
    }

    @PostMapping("/chatroom/{userId}")
    fun createChatroom(authentication :Authentication, @PathVariable userId: Long) :ResponseEntity<CommonResponse<CreateChatRoomResponse>> {
        val id = authentication.principal as Long
        logger.info { "채팅방 생성 요청 시작 - 요청 사용자 ID: $id, 대상 사용자 ID: $userId" }

        val createChatroom : CreateChatRoomResponse = CreateChatRoomResponse.fromModel(chatService.saveChatRoom(id , userId))
        logger.info { "채팅방 생성 완료 - 생성된 채팅방 ID: ${createChatroom.chatRoomId}" }

        return CommonResponse.created("채팅방 생성 성공", createChatroom)
    }

    @GetMapping("/chatroom")
    fun getChatroom(authentication :Authentication) :ResponseEntity<CommonResponse<List<GetChatRoomResponse>>> {
        val userId = authentication.principal as Long
        logger.info { "채팅방 조회 요청 시작 - userId: $userId" }

        val chatrooms :List<GetChatRoomResponse>? = chatService.findMyChatrooms(userId)
        if (chatrooms.isNullOrEmpty()) {
            logger.warn { "조회된 채팅방 없음 - userId: $userId" }
        } else {
            logger.info { "채팅방 조회 완료 - userId: $userId, chatrooms: $chatrooms" }
        }

        return CommonResponse.ok("채팅방 조회 성공", chatrooms)
    }

    @GetMapping("/messages/{roomId}")
    fun getMessages(@PathVariable roomId: Long) :ResponseEntity<CommonResponse<List<MessageResponse>>> {
        logger.info { "메시지 조회 요청 시작 - roomId: $roomId" }

        val messages = chatService.findMessagesByChatroomId(roomId)
        if (messages.isNullOrEmpty()) {
            logger.warn { "조회된 메시지 없음 - roomId: $roomId" }
        } else {
            logger.info { "메시지 조회 완료 - roomId: $roomId, messages: $messages" }
        }

        return CommonResponse.ok("메세지 조회 성공", messages)
    }
}