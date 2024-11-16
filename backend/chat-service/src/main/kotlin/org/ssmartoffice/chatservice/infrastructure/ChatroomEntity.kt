package org.ssmartoffice.chatservice.infrastructure

import jakarta.persistence.*
import org.ssmartoffice.chatservice.domain.ChatRoom
import org.ssmartoffice.chatservice.domain.Message
import java.time.LocalDateTime

@Entity
@Table(name = "chatrooms")
class ChatroomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CHATROOM_ID")
    val id: Long? = null,
) {

    @Column(nullable = false, updatable = false)
    private var createdDateTime: LocalDateTime? = null
        private set

    @PrePersist
    fun onCreate() {
        val currentTime = LocalDateTime.now()
        createdDateTime = currentTime
    }

    companion object {
        fun fromModel(chatroom: ChatRoom): ChatroomEntity {
            return ChatroomEntity(
                id = chatroom.id
            )
        }
    }

    fun toModel(): ChatRoom {
        return ChatRoom(
            id = id,
            createdDateTime = createdDateTime,
        )
    }
}