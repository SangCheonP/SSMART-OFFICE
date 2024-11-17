package org.ssmartoffice.chatservice.domain

import java.time.LocalDateTime

class Message(
    val id :Long? = null,
    val userId :Long,
    val chatroom :UserChatRoom,
    val type :MessageType,
    val content :String,
    val deleted :Boolean = false,
    val createdAt :LocalDateTime = LocalDateTime.now()
)