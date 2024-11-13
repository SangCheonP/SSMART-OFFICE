package org.ssmartoffice.assignmentservice.controller.response

import com.fasterxml.jackson.annotation.JsonInclude
import org.ssmartoffice.assignmentservice.domain.Assignment
import org.ssmartoffice.assignmentservice.domain.AssignmentType
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
    val date: String? = null
) {
    companion object {
        private val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")

        fun fromModel(
            assignment: Assignment,
            includeDescription: Boolean = true,
            includeDate: Boolean = false
        ): AssignmentInfoResponse {
            return AssignmentInfoResponse(
                id = assignment.id,
                name = assignment.name,
                description = if (includeDescription) assignment.description else null,
                type = assignment.type,
                completed = assignment.completed,
                date = if (includeDate) assignment.date else null
            )
        }
    }
}