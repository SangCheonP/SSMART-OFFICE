package org.ssmartoffice.assignmentservice.controller.response

import com.fasterxml.jackson.annotation.JsonInclude
import org.ssmartoffice.assignmentservice.domain.Assignment
import org.ssmartoffice.assignmentservice.domain.AssignmentType
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AssignmentInfoResponse(
    val id: Long = 0,
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    val name: String = "",
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val description: String? = null,
    val type: AssignmentType,
    val completed: Boolean = false,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val date: LocalDate? = null
) {
    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        fun fromModel(
            assignment: Assignment,
        ): AssignmentInfoResponse {
            return AssignmentInfoResponse(
                id = assignment.id,
                name = assignment.name,
                description = assignment.description,
                type = assignment.type,
                completed = assignment.completed,
                date = assignment.date?.let { LocalDate.parse(it, formatter) }
            )
        }
    }
}