package org.ssmartoffice.chatservice.controller.response

class CreateChatRoomResponse(
    val chatRoomId: Long
){
    companion object {
        fun fromModel(id: Long): CreateChatRoomResponse {
            return CreateChatRoomResponse(
                chatRoomId = id
            )
        }
    }
}