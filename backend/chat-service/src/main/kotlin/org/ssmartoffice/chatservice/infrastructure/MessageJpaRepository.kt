package org.ssmartoffice.chatservice.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageJpaRepository: JpaRepository<MessageEntity, Long> {
}