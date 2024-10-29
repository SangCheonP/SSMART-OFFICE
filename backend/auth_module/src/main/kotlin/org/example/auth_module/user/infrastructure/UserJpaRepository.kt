package org.example.auth_module.user.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String?): UserEntity?
    fun findByLoginId(loginId: String?): UserEntity?
}
