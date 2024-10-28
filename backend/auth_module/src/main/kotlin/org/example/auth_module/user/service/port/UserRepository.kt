package org.example.auth_module.user.service.port

import org.example.auth_module.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String?): User?
    fun findByLoginId(loginId: String): User?
}
