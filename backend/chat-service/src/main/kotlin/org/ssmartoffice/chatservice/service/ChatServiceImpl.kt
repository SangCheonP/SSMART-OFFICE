package org.ssmartoffice.chatservice.service

import org.springframework.stereotype.Service
import org.ssmartoffice.chatservice.controller.port.ChatService
import org.ssmartoffice.chatservice.domain.ChatRoom
import org.ssmartoffice.chatservice.domain.Message
import org.ssmartoffice.chatservice.domain.UserChatRoom
import org.ssmartoffice.chatservice.infrastructure.UserChatRoomJpaRepository
import org.ssmartoffice.chatservice.service.port.ChatRepository

@Service
class ChatServiceImpl(
    val chatRepository: ChatRepository,
    private val userChatRoomJpaRepository: UserChatRoomJpaRepository,
): ChatService {
    override fun saveMessage(message: Message) {
        chatRepository.saveMessage(message)
    }

    override fun saveChatRoom(currentUserId: Long, userId: Long) :Long{

        val userChatRoom :UserChatRoom? = chatRepository.findUserChatRoom(currentUserId, userId)

        if(userChatRoom != null){
            return userChatRoom.chatroomId!!
        }

        val roomId = chatRepository.saveChatRoom(ChatRoom())?: throw Exception("채팅방 생성 실패")
        chatRepository.saveUserChatRoom(UserChatRoom(userId = userId, chatroomId = roomId))
        chatRepository.saveUserChatRoom(UserChatRoom(userId = currentUserId, chatroomId = roomId))
        return roomId
    }

    override fun findChatroom(roomId: Long): ChatRoom {
        return chatRepository.findChatRoomById(roomId)
    }
}