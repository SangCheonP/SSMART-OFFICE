package org.example.auth_module.user.service.port

import org.example.auth_module.user.domain.User

interface UserRepository {
    fun findByEmail(email: String?): User?
    fun findByLoginId(loginId: String): User?
    fun save(user: User): User
}
