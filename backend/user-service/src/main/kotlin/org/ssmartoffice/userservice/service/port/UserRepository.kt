package org.ssmartoffice.userservice.service.port

import org.ssmartoffice.userservice.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.ssmartoffice.userservice.domain.Role

@Repository
interface UserRepository {
    fun save(user: User): User
    fun findById(id: Long): User?
    fun findByEmail(email: String): User?
    fun findAll(pageable: Pageable): Page<User>
    fun existsByEmail(adminEmail: String): Boolean
    fun findByIdIn(ids: List<Long>): List<User>
    fun findByRoleNot(role: Role, pageable: Pageable): Page<User>
    fun existsById(userId: Long): Boolean
    fun findUser(keyword: String, pageable: Pageable): Page<User>
}