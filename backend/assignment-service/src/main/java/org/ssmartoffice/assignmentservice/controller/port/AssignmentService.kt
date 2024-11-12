package org.ssmartoffice.assignmentservice.controller.port

import org.ssmartoffice.assignmentservice.domain.Assignment
import org.ssmartoffice.assignmentservice.controller.request.AssignmentRegisterRequest

interface AssignmentService {
    fun addAssignment(userId: Long, assignmentRegisterRequest: AssignmentRegisterRequest): Assignment
}