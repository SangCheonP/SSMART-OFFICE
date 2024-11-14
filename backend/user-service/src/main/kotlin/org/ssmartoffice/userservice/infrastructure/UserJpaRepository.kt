package org.ssmartoffice.userservice.infrastructure

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.ssmartoffice.userservice.domain.Role
import org.ssmartoffice.userservice.domain.User

@Repository
interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity
    fun existsByEmail(adminEmail: String): Boolean
    fun findAllByIdIn(ids: List<Long>): List<UserEntity>
    fun findByRoleNot(admin: Role, pageable: Pageable): Page<User>
}
