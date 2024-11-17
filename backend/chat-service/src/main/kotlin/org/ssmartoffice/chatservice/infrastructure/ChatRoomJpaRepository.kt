package org.ssmartoffice.chatservice.infrastructure

import feign.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.util.*

interface ChatRoomJpaRepository :JpaRepository<ChatroomEntity, Long> {

    override fun findById(id: Long): Optional<ChatroomEntity>
}
