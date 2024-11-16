package org.ssmartoffice.chatservice.infrastructure

import org.springframework.stereotype.Repository
import org.ssmartoffice.chatservice.domain.ChatRoom
import org.ssmartoffice.chatservice.domain.Message
import org.ssmartoffice.chatservice.domain.UserChatRoom
import org.ssmartoffice.chatservice.service.port.ChatRepository

@Repository
class ChatRepositoryImpl(
    val messageJpaRepository: MessageJpaRepository,
    val chatRoomJpaRepository: ChatRoomJpaRepository,
    val userChatRoomJpaRepository: UserChatRoomJpaRepository
) :ChatRepository {

    override fun saveMessage(message: Message) {
        messageJpaRepository.save(MessageEntity.fromModel(message))
    }

    override fun saveChatRoom(chatRoom: ChatRoom) :Long? {
        return chatRoomJpaRepository.save(ChatroomEntity.fromModel(chatRoom)).id
    }

    override fun findChatRoomById(id: Long): ChatRoom {
        chatRoomJpaRepository.findById(id)
            .orElseThrow { IllegalArgumentException("ChatRoom not found") }
            .let { return it.toModel() }
    }

    override fun saveUserChatRoom(userChatRoom: UserChatRoom): UserChatRoom? {
        return userChatRoomJpaRepository.save(UserChatroomEntity.fromModel(userChatRoom)).toModel()
    }

    override fun findUserChatRoom(myId: Long, userId: Long): UserChatRoom? {
        return userChatRoomJpaRepository.findByMyIdAndUserId(myId, userId)?.toModel()
    }

}