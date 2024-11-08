package org.ssmartoffice.userservice.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.ssmartoffice.userservice.infrastructure.UserEntity

@Repository
interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity
    fun findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc(prefix: String): UserEntity
    fun existsByEmail(adminEmail: String): Boolean
}
