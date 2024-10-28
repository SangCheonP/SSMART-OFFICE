package org.example.auth_module.user.infrastructure

import org.example.auth_module.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository : JpaRepository<User?, Long?> {
    fun findByEmail(email: String?): User?

    fun findByLoginId(loginId: String?): User?
}
