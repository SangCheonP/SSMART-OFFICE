package org.ssmartoffice.chatservice.controller.port

import org.ssmartoffice.chatservice.controller.response.GetChatRoomResponse
import org.ssmartoffice.chatservice.controller.response.MessageResponse
import org.ssmartoffice.chatservice.domain.ChatRoom
import org.ssmartoffice.chatservice.domain.Message
import org.ssmartoffice.chatservice.domain.UserChatRoom

interface ChatService {
    fun saveMessage(message: Message)
    fun saveChatRoom(currentUserId: Long, userId: Long): Long
    fun findUserChatroom(userId: Long, roomId: Long): UserChatRoom?
    fun findMyChatrooms(userId: Long): List<GetChatRoomResponse>?
    fun findMessagesByChatroomId(roomId: Long): List<MessageResponse>?
}