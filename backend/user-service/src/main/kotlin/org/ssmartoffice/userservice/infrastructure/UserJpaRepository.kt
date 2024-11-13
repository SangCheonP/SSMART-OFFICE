package org.ssmartoffice.userservice.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity
    fun findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc(prefix: String): UserEntity
    fun existsByEmail(adminEmail: String): Boolean
    fun findAllByIdIn(ids: List<Long>): List<UserEntity>
}
