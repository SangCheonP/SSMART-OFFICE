package org.ssmartoffice.chatservice.controller

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
import org.ssmartoffice.chatservice.global.dto.CommonResponse
import mu.KotlinLogging

@RestController
@RequestMapping("/api/v1/chats")
class ChatController(
    private val sendingOperations: SimpMessageSendingOperations,
    val chatService: ChatService
) {
    private val logger = KotlinLogging.logger {}

    @MessageMapping("/{roomId}")
    fun enter(messageRequest: MessageRequest, authentication: Authentication, @DestinationVariable roomId: Long) {
        val userId: Long = authentication.principal as Long
        logger.info { "사용자 ID: $userId, 채팅방 ID: $roomId, 요청 메시지: $messageRequest" }

        val userChatroom = chatService.findUserChatroom(userId, roomId)
        if (userChatroom == null) {
            logger.error { "채팅방 ID: $roomId에 대한 채팅방이 존재하지 않습니다." }
            throw Exception("채팅방이 존재하지 않습니다.")
        }
        logger.info { "채팅방 ID: $roomId가 존재합니다. 메시지 생성 시작." }

        val message = Message(
            chatroom = userChatroom,
            userId = userId,
            content = messageRequest.content,
            type = messageRequest.type,
        )
        chatService.saveMessage(message)
        logger.info { "메시지 저장 완료. 내용: ${message.content}, 타입: ${message.type}" }

        sendingOperations.convertAndSend("/api/v1/chats/ws/topic/$roomId", message)
        logger.info { "메시지가 채팅방 /topic/$roomId 경로로 발행되었습니다." }
    }

    @PostMapping("/chatroom/{userId}")
    fun createChatroom(authentication: Authentication, @PathVariable userId: Long): ResponseEntity<CommonResponse<CreateChatRoomResponse>> {
        val id = authentication.principal as Long
        logger.info { "채팅방 생성 요청. 요청 사용자 ID: $id, 대상 사용자 ID: $userId" }

        val createChatroom: CreateChatRoomResponse = CreateChatRoomResponse.fromModel(chatService.saveChatRoom(id, userId))
        logger.info { "채팅방 생성 성공. 생성된 채팅방 ID: ${createChatroom.chatroomId}" }

        return CommonResponse.created("채팅방 생성 성공", createChatroom)
    }

    @GetMapping("/chatroom")
    fun getChatroom(authentication: Authentication): ResponseEntity<CommonResponse<List<GetChatRoomResponse>>> {
        val userId = authentication.principal as Long
        logger.info { "사용자 ID: $userId의 채팅방 조회 요청." }

        val chatrooms: List<GetChatRoomResponse>? = chatService.findMyChatrooms(userId)
        if (chatrooms.isNullOrEmpty()) {
            logger.warn { "사용자 ID: $userId는 생성된 채팅방이 없습니다." }
        } else {
            logger.info { "조회된 채팅방 목록: $chatrooms" }
        }

        return CommonResponse.ok("채팅방 조회 성공", chatrooms)
    }

    @GetMapping("/messages/{roomId}")
    fun getMessages(@PathVariable roomId: Long): ResponseEntity<CommonResponse<List<MessageResponse>>> {
        logger.info { "채팅방 ID: $roomId의 메시지 조회 요청." }

        val messages = chatService.findMessagesByChatroomId(roomId)
        if (messages.isNullOrEmpty()) {
            logger.warn { "채팅방 ID: $roomId에 메시지가 없습니다." }
        } else {
            logger.info { "조회된 메시지 목록: $messages" }
        }

        return CommonResponse.ok("메세지 조회 성공", messages)
    }
}