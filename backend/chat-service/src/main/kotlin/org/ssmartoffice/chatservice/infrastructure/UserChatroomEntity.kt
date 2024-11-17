package org.ssmartoffice.chatservice.infrastructure

import jakarta.persistence.*
import org.ssmartoffice.chatservice.domain.UserChatRoom

@Entity
@Table(name = "userchatrooms")
class UserChatroomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val userId: Long?,

    @ManyToOne
    @JoinColumn(name = "USER_CHATROOM_ID")
    val chatroom: ChatroomEntity,
) {
    fun toModel(): UserChatRoom? {
        return UserChatRoom(
            id = id,
            userId = userId,
            chatroomId = chatroom.id
        )
    }

    companion object {
        fun fromModel(userChatroom: UserChatRoom): UserChatroomEntity {
            return UserChatroomEntity(
                id = userChatroom.id,
                userId = userChatroom.userId,
                chatroom = ChatroomEntity(id = userChatroom.chatroomId)
            )
        }
    }
}