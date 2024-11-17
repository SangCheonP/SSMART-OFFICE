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

    override fun findUserChatRoomById(userId: Long, roomid: Long): UserChatRoom? {
        return userChatRoomJpaRepository.findByRoomId(userId, roomid)?.toModel()
    }

    override fun saveUserChatRoom(userChatRoom: UserChatRoom): UserChatRoom? {
        return userChatRoomJpaRepository.save(UserChatroomEntity.fromModel(userChatRoom)).toModel()
    }

    override fun findUserChatRoom(myId: Long, userId: Long): UserChatRoom? {
        return userChatRoomJpaRepository.findByMyIdAndUserId(myId, userId)?.toModel()
    }

    override fun findAllUserChatRoom(userId: Long): List<UserChatRoom>? {
        return userChatRoomJpaRepository.findAllByUserId(userId)?.map { it.toModel()!! }
    }

    override fun findLastMessageByChatroomId(chatRoomId: Long): Message? {
        return messageJpaRepository.findTopByChatroomIdOrderByCreatedAtDesc(chatRoomId)?.toModel()
    }

    override fun findMessagesByChatroomId(roomId: Long): List<Message>?
    {
        return messageJpaRepository.findAllByChatroomId(roomId).map { it.toModel() }
    }
}