package org.ssmartoffice.userservice.controller.response

import org.ssmartoffice.userservice.domain.Role
import org.ssmartoffice.userservice.domain.Assignment

class AssignmentDetailResponse(
    val id: Long = 0,
    val employeeNumber: String,
    val email: String,
    val name: String,
    val position: String,
    val duty: String,
    val profileImageUrl: String,
    var role: Role,
    val status: UserStatus,
    val phoneNumber:String?
) {
    companion object {
        fun fromModel(assignment: Assignment): AssignmentDetailResponse {
            return AssignmentDetailResponse(
                id = assignment.id,
                email = assignment.email,
                name = assignment.name,
                position = assignment.position,
                duty = assignment.duty,
                profileImageUrl = assignment.profileImageUrl,
                role = assignment.role,
                employeeNumber = assignment.employeeNumber,
                status = assignment.status,
                phoneNumber = assignment.phoneNumber
            )
        }
    }
}
