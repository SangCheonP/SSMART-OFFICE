package org.ssmartoffice.chatservice.service.port

import org.ssmartoffice.chatservice.domain.ChatRoom
import org.ssmartoffice.chatservice.domain.Message
import org.ssmartoffice.chatservice.domain.UserChatRoom

interface ChatRepository  {
    fun saveMessage(message :Message)
    fun saveChatRoom(chatRoom: ChatRoom) :Long?
    fun findChatRoomById(id: Long): ChatRoom
    fun saveUserChatRoom(userChatRoom: UserChatRoom): UserChatRoom?
    fun findUserChatRoom(myId: Long, userId: Long): UserChatRoom?
}