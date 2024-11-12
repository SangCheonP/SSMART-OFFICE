package org.ssmartoffice.assignmentservice.service.port

import org.ssmartoffice.assignmentservice.domain.Assignment
import org.springframework.stereotype.Repository

@Repository
interface AssignmentRepository {
    fun save(assignment: Assignment): Assignment
    fun findByUserIdAndDate(userId: Long, date: String): List<Assignment>
    fun findByUserIdAndDateBetween(userId: Long, startDate: String, endDate: String): List<Assignment>
    fun findById(assignmentId: Long): Assignment
}