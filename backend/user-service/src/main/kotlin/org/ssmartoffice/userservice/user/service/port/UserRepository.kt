package org.ssmartoffice.userservice.user.service.port

import org.ssmartoffice.userservice.user.domain.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface UserRepository {
    fun save(user: User): User
    fun findById(id: Long): User?
    fun findByEmail(email: String): User?
    fun findAll(pageable: Pageable): Page<User>
    fun findMaxEmployeeNumberByYear(s: String): String?
}