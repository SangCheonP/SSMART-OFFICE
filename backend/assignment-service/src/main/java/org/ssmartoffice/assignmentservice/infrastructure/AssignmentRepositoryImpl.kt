package org.ssmartoffice.assignmentservice.infrastructure

import org.ssmartoffice.assignmentservice.domain.Assignment
import org.ssmartoffice.assignmentservice.service.port.AssignmentRepository
import org.springframework.stereotype.Repository

@Repository
class AssignmentRepositoryImpl(
    private val assignmentJpaRepository: AssignmentJpaRepository,
) : AssignmentRepository {
    override fun save(assignment: Assignment): Assignment {
        return assignmentJpaRepository.save(AssignmentEntity.fromModel(assignment)).toModel()
    }

    override fun findByUserIdAndDate(userId: Long, date: String): List<Assignment> {
        return assignmentJpaRepository.findByUserIdAndDate(userId, date).map { assignmentEntity ->
            assignmentEntity.toModel()
        }
    }
}