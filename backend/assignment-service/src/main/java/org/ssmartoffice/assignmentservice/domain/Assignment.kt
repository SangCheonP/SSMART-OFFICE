package org.ssmartoffice.assignmentservice.domain

import org.ssmartoffice.assignmentservice.controller.request.AssignmentRegisterRequest
import java.time.format.DateTimeFormatter

class Assignment(
    val id: Long = 0,
    val userId: Long = 0,
    val name: String = "",
    val date: String = "",
    val type: AssignmentType = AssignmentType.TASK,
    val description: String = "",
    var completed: Boolean = false,
    val deleted: Boolean = false
) {

    fun toggleCompleteStatus() {
        completed = !completed
    }

    companion object {

        fun fromRequest(
            userId: Long,
            assignmentRegisterRequest: AssignmentRegisterRequest
        ): Assignment {
            return Assignment(
                userId = userId,
                name = assignmentRegisterRequest.name,
                date = assignmentRegisterRequest.date.format(DateTimeFormatter.ofPattern("yyyyMMdd")),
                type = assignmentRegisterRequest.type,
                description = assignmentRegisterRequest.description
            )
        }
    }
}
