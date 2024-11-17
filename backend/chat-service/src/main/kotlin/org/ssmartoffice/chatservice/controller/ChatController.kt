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
        logger.info { "userId: $userId, roomId: $roomId" }
        val userChatroom = chatService.findUserChatroom(userId, roomId) ?: throw Exception("채팅방이 존재하지 않습니다.")

        val message = Message(
            chatroom = userChatroom,
            userId = userId,
            content = messageRequest.content,
            type = messageRequest.type,
        )

        chatService.saveMessage(message)

        sendingOperations.convertAndSend("/api/v1/chats/ws/topic/$roomId", message)
    }

    @PostMapping("/chatroom/{userId}")
    fun createChatroom(authentication :Authentication, @PathVariable userId: Long) :ResponseEntity<CommonResponse<CreateChatRoomResponse>> {
        val id = authentication.principal as Long
        logger.info { "userId: $userId , id: $id" }
        val createChatroom : CreateChatRoomResponse = CreateChatRoomResponse.fromModel(chatService.saveChatRoom(id , userId))
        return CommonResponse.created("채팅방 생성 성공", createChatroom)
    }

    @GetMapping("/chatroom")
    fun getChatroom(authentication :Authentication) :ResponseEntity<CommonResponse<List<GetChatRoomResponse>>> {
        val userId = authentication.principal as Long
        val chatrooms :List<GetChatRoomResponse>? = chatService.findMyChatrooms(userId)
        return CommonResponse.ok("채팅방 조회 성공", chatrooms)
    }

    @GetMapping("/messages/{roomId}")
    fun getMessages(@PathVariable roomId: Long) :ResponseEntity<CommonResponse<List<MessageResponse>>> {
        val messages = chatService.findMessagesByChatroomId(roomId)
        return CommonResponse.ok("메세지 조회 성공", messages)
    }
}