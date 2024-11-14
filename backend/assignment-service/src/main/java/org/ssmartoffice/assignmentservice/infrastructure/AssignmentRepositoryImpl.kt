package org.ssmartoffice.assignmentservice.infrastructure

import org.ssmartoffice.assignmentservice.domain.Assignment
import org.ssmartoffice.assignmentservice.service.port.AssignmentRepository
import org.springframework.stereotype.Repository
import org.ssmartoffice.assignmentservice.global.const.errorcode.AssignmentErrorCode
import org.ssmartoffice.assignmentservice.global.exception.AssignmentException

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

    override fun findByUserIdAndDateBetween(
        userId: Long,
        startDate: String,
        endDate: String
    ): List<Assignment> {
        return assignmentJpaRepository.findByUserIdAndDateBetween(userId, startDate, endDate).map { assignmentEntity ->
            assignmentEntity.toModel()
        }
    }

    override fun findById(assignmentId: Long): Assignment {
        return assignmentJpaRepository.findById(assignmentId)
            .orElseThrow { AssignmentException(AssignmentErrorCode.NOT_FOUND_ASSIGNMENT) }
            .toModel()
    }
}