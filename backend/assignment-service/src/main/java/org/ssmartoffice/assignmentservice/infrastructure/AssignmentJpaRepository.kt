package org.ssmartoffice.assignmentservice.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AssignmentJpaRepository : JpaRepository<AssignmentEntity, Long> {
    fun findByUserIdAndDate(userId: Long, date: String): List<AssignmentEntity>
}
