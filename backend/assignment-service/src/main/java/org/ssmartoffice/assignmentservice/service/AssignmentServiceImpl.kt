package org.ssmartoffice.assignmentservice.service

import org.ssmartoffice.assignmentservice.controller.port.AssignmentService
import org.ssmartoffice.assignmentservice.domain.Assignment
import org.ssmartoffice.assignmentservice.service.port.AssignmentRepository
import org.springframework.stereotype.Service
import org.ssmartoffice.assignmentservice.controller.request.AssignmentRegisterRequest

@Service
class AssignmentServiceImpl(
    val assignmentRepository: AssignmentRepository,
) : AssignmentService {

    override fun addAssignment(userId: Long, assignmentRegisterRequest: AssignmentRegisterRequest): Assignment {
        val assignment = Assignment.fromRequest(userId, assignmentRegisterRequest)
        return assignmentRepository.save(assignment)
    }
}