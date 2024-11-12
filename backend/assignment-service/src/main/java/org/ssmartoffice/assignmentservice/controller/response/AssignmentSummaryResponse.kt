package org.ssmartoffice.assignmentservice.controller.response

import org.ssmartoffice.assignmentservice.domain.Assignment
import org.ssmartoffice.assignmentservice.domain.AssignmentType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class AssignmentSummaryResponse(
    val id: Long = 0,
    var name: String,
    var date: LocalDate,
    var type: AssignmentType,
) {
    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        fun fromModel(assignment: Assignment): AssignmentSummaryResponse {
            return AssignmentSummaryResponse(
                id = assignment.id,
                name = assignment.name,
                date = LocalDate.parse(assignment.date, formatter),
                type = assignment.type
            )
        }
    }
}
