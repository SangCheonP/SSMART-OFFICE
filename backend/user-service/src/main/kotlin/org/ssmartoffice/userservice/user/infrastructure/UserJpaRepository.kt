package org.ssmartoffice.userservice.user.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity
    fun findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc(prefix: String): UserEntity
}