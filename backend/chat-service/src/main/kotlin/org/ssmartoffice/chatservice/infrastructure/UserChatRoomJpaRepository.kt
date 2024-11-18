package org.ssmartoffice.chatservice.infrastructure

import feign.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserChatRoomJpaRepository :JpaRepository<UserChatroomEntity, Long>{
    @Query("select u from UserChatroomEntity u " +
            "where u.chatroom.id in (" +
            "select u.chatroom.id from UserChatroomEntity u where u.userId = :myId)" +
            "and u.userId = :userId")
    fun findByMyIdAndUserId(@Param("myId") myId :Long, @Param("userId") userId :Long): UserChatroomEntity?


    @Query("select u from UserChatroomEntity u " +
            "where u.chatroom.id in (" +
            "select u.chatroom.id from UserChatroomEntity u where u.userId = :userId)" +
            "and u.userId != :userId")
    fun findAllByUserId(userId: Long): List<UserChatroomEntity>?

    @Query("select u from UserChatroomEntity u \n" +
            "where u.chatroom.id = :roomId and u.userId = :userId")
    fun findByRoomId(userId: Long, roomId: Long): UserChatroomEntity?
}
