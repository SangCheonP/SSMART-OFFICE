package org.ssmartoffice.userservice.controller.response

import org.ssmartoffice.userservice.domain.Assignment

data class AssignmentInfoResponse(
    val id: Long = 0,
    var name: String,
    var position: String,
    var duty: String,
){
    companion object {
        fun fromModel(assignment: Assignment): AssignmentInfoResponse {
            return AssignmentInfoResponse(
                id = assignment.id,
                name = assignment.name,
                position = assignment.position,
                duty = assignment.duty
            )
        }
    }
}
