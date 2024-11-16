package org.ssmartoffice.chatservice.controller.port

import org.ssmartoffice.chatservice.domain.ChatRoom
import org.ssmartoffice.chatservice.domain.Message

interface ChatService {
    fun saveMessage(message :Message)
    fun saveChatRoom(currentUserId: Long, userId: Long): Long

    fun findChatroom(roomId: Long): ChatRoom
}