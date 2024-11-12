package org.ssmartoffice.userservice.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository : JpaRepository<AssignmentEntity, Long> {
    fun findByEmail(email: String): AssignmentEntity
    fun findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc(prefix: String): AssignmentEntity
    fun existsByEmail(adminEmail: String): Boolean
    fun findAllByIdIn(ids: List<Long>): List<AssignmentEntity>
}
