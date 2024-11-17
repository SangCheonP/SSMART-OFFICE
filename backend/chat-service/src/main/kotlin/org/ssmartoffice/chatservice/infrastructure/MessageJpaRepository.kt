package org.ssmartoffice.chatservice.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface MessageJpaRepository: JpaRepository<MessageEntity, Long> {
    @Query("select m from MessageEntity m where m.chatroom.chatroom.id = :chatroomId order by m.createdAt desc limit 1")
    fun findTopByChatroomIdOrderByCreatedAtDesc(chatroomId: Long): MessageEntity?

    @Query("select m from MessageEntity m where m.chatroom.chatroom.id = :chatroomId")
    fun findAllByChatroomId(chatroomId: Long): List<MessageEntity>
}