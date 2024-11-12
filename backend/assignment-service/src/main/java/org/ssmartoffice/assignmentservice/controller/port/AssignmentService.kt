package org.ssmartoffice.userservice.controller.port

import org.ssmartoffice.userservice.domain.Assignment
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable
import org.ssmartoffice.assignmentservice.controller.request.AssignmentRegisterRequest
import org.ssmartoffice.userservice.controller.request.*

interface AssignmentService {
    fun addAssignment(assignmentRegisterRequest: AssignmentRegisterRequest): Assignment
}