package org.ssmartoffice.chatservice.controller

import org.springframework.messaging.handler.annotation.DestinationVariable
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ssmartoffice.chatservice.controller.port.ChatService
import org.ssmartoffice.chatservice.controller.request.MessageRequest
import org.ssmartoffice.chatservice.domain.Message


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

        val chatroom = chatService.findChatroom(roomId)

        val message = Message(
            chatroom = chatroom,
            userId = userId,
            content = messageRequest.content,
            type = messageRequest.type,
        )

        sendingOperations.convertAndSend("/api/v1/chats/ws/topic/$roomId", message)
    }

    @PostMapping("/chatroom/{userId}")
    fun createChatroom(authentication :Authentication, @PathVariable userId: Long) {
        val id = authentication.principal as Long
        chatService.saveChatRoom(id , userId)
    }


}