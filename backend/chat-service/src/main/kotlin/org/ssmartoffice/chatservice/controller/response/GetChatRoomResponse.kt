package org.ssmartoffice.chatservice.controller.response

import java.time.LocalDateTime

class GetChatRoomResponse(
    val chatRoomId: Long,
    val chatRoomMemberId: Long,
    val lastMessage: String? = null,
    val lastMessageTime: LocalDateTime? = null
)