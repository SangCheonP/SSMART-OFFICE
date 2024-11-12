package org.ssmartoffice.userservice.domain

import org.ssmartoffice.assignmentservice.controller.request.AssignmentRegisterRequest
import org.ssmartoffice.assignmentservice.domain.AssignmentType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Assignment(
    val id: Long = 0,
    val userId: Long = 0,
    val name: String = "",
    val date: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),
    val type: AssignmentType = AssignmentType.TASK,
    val description: String = "",
    val completed: Boolean = false,
    val deleted: Boolean = false
) {

    companion object {

        fun fromRequest(
            assignmentRegisterRequest: AssignmentRegisterRequest
        ): Assignment {
            return Assignment(
                email = userRegisterRequest.email,
                password = userRegisterRequest.password,
                name = userRegisterRequest.name,
                position = userRegisterRequest.position,
                duty = userRegisterRequest.duty,
                profileImageUrl = userRegisterRequest.profileImageUrl,
                employeeNumber = userRegisterRequest.employeeNumber,
                phoneNumber = userRegisterRequest.phoneNumber
            )
        }
    }
}
