package org.ssmartoffice.chatservice.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.ssmartoffice.chatservice.controller.port.ChatService
import org.ssmartoffice.chatservice.controller.response.GetChatRoomResponse
import org.ssmartoffice.chatservice.controller.response.MessageResponse
import org.ssmartoffice.chatservice.domain.ChatRoom
import org.ssmartoffice.chatservice.domain.Message
import org.ssmartoffice.chatservice.domain.UserChatRoom
import org.ssmartoffice.chatservice.infrastructure.UserChatRoomJpaRepository
import org.ssmartoffice.chatservice.service.port.ChatRepository

val logger = KotlinLogging.logger {}

@Service
class ChatServiceImpl(
    val chatRepository: ChatRepository,
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

    override fun findUserChatroom(userId:Long, roomId: Long): UserChatRoom? {
        return chatRepository.findUserChatRoomById(userId, roomId)
    }

    override fun findMyChatrooms(userId: Long): List<GetChatRoomResponse>? {
        val userChatRooms = chatRepository.findAllUserChatRoom(userId)

        if (userChatRooms != null) {
            return userChatRooms.map {
                val lastChat = chatRepository.findLastMessageByChatroomId(it.chatroomId!!)

                GetChatRoomResponse(
                    chatRoomId = it.chatroomId,
                    chatRoomMemberId = it.userId!!,
                    lastMessage = lastChat?.content,
                    lastMessageTime = lastChat?.createdAt
                )
            }.sortedByDescending { it.lastMessageTime }
        }

        return null
    }

    override fun findMessagesByChatroomId(roomId: Long): List<MessageResponse>? {
        return chatRepository.findMessagesByChatroomId(roomId)?.map { MessageResponse.fromModel(it) }
    }

}