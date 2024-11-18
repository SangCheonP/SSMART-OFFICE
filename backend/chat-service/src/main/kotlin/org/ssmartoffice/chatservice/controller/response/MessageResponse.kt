package org.ssmartoffice.chatservice.controller.response

import org.ssmartoffice.chatservice.domain.MessageType
import org.ssmartoffice.chatservice.domain.UserChatRoom
import java.time.LocalDateTime

class MessageResponse(
    val id :Long? = null,
    val userId :Long,
    val chatroom : UserChatRoom,
    val type : MessageType,
    val content :String,
    val createdAt : LocalDateTime = LocalDateTime.now()
) {

    companion object {
        fun fromModel(message: org.ssmartoffice.chatservice.domain.Message): MessageResponse {
            return MessageResponse(
                id = message.id,
                userId = message.userId,
                chatroom = message.chatroom,
                type = message.type,
                content = message.content,
                createdAt = message.createdAt
            )
        }
    }
}