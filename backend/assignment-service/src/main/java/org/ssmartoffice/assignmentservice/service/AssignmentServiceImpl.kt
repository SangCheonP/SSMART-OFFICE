package org.ssmartoffice.assignmentservice.service

import org.ssmartoffice.assignmentservice.controller.port.AssignmentService
import org.ssmartoffice.assignmentservice.domain.Assignment
import org.ssmartoffice.assignmentservice.service.port.AssignmentRepository
import org.springframework.stereotype.Service
import org.ssmartoffice.assignmentservice.controller.request.AssignmentRegisterRequest
import org.ssmartoffice.assignmentservice.global.const.errorcode.AssignmentErrorCode
import org.ssmartoffice.assignmentservice.global.exception.AssignmentException
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Service
class AssignmentServiceImpl(
    val assignmentRepository: AssignmentRepository,
) : AssignmentService {

    override fun addAssignment(userId: Long, assignmentRegisterRequest: AssignmentRegisterRequest): Assignment {
        val assignment = Assignment.fromRequest(userId, assignmentRegisterRequest)
        return assignmentRepository.save(assignment)
    }

    override fun findUserAssignmentByDate(userId: Long, month: String, day: String): List<Assignment> {
        val formattedDate = "$month$day"
        return assignmentRepository.findByUserIdAndDate(userId, formattedDate)
    }

    override fun findUserAssignmentByDate(userId: Long, month: String): List<Assignment> {
        val yearMonth = YearMonth.parse(month, DateTimeFormatter.ofPattern("yyyyMM"))
        val startFormattedDate = "$month$01"
        val endFormattedDate = "$month${yearMonth.lengthOfMonth()}"
        return assignmentRepository.findByUserIdAndDateBetween(userId, startFormattedDate, endFormattedDate)
    }

    override fun toggleAssignmentStatus(userId: Long, assignmentId: Long): Assignment {
        val assignment: Assignment = assignmentRepository.findById(assignmentId)
        if (assignment.userId != userId) {
            throw AssignmentException(AssignmentErrorCode.ACCESS_DENIED_ASSIGNMENT)
        }
        assignment.toggleCompleteStatus()
        assignmentRepository.save(assignment)
        return assignment
    }
}