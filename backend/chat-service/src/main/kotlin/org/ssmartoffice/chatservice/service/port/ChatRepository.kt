package org.ssmartoffice.chatservice.service.port

import org.ssmartoffice.chatservice.domain.ChatRoom
import org.ssmartoffice.chatservice.domain.Message
import org.ssmartoffice.chatservice.domain.UserChatRoom

interface ChatRepository  {
    fun saveMessage(message :Message)
    fun saveChatRoom(chatRoom: ChatRoom) :Long?
    fun findUserChatRoomById(userId: Long, roomId: Long): UserChatRoom?
    fun saveUserChatRoom(userChatRoom: UserChatRoom): UserChatRoom?
    fun findUserChatRoom(myId: Long, userId: Long): UserChatRoom?
    fun findAllUserChatRoom(userId: Long): List<UserChatRoom>?
    fun findLastMessageByChatroomId(chatRoomId: Long): Message?
    fun findMessagesByChatroomId(roomId: Long): List<Message>?
}