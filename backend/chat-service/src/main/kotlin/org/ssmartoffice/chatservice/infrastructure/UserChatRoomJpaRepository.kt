package org.ssmartoffice.chatservice.infrastructure

import feign.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserChatRoomJpaRepository :JpaRepository<UserChatroomEntity, Long>{
    @Query("select u from UserChatroomEntity u \n" +
            "where u.chatroom.id = (select u.chatroom.id from UserChatroomEntity u where u.userId = :myId)\n" +
            "and u.userId = :userId")
    fun findByMyIdAndUserId(@Param("myId") myId :Long, @Param("userId") userId :Long): UserChatroomEntity?
}
