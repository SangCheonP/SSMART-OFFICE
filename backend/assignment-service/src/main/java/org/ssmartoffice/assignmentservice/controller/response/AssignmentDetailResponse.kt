package org.ssmartoffice.assignmentservice.controller.response

import org.ssmartoffice.userservice.domain.Role
import org.ssmartoffice.assignmentservice.domain.Assignment
import org.ssmartoffice.assignmentservice.domain.AssignmentType

class AssignmentDetailResponse(
    val id: Long = 0,
    val name: String,
    val description: String,
    val type: AssignmentType,
    val completed: Boolean
) {
    companion object {
        fun fromModel(assignment: Assignment): AssignmentDetailResponse {
            return AssignmentDetailResponse(
                id = assignment.id,
                name = assignment.name,
                description = assignment.description,
                type = assignment.type,
                completed = assignment.completed
            )
        }
    }
}
