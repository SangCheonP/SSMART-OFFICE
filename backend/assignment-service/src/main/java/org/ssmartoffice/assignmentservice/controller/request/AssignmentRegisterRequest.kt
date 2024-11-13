package org.ssmartoffice.assignmentservice.controller.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.ssmartoffice.assignmentservice.domain.AssignmentType
import java.time.LocalDate

data class AssignmentRegisterRequest(
    @field:NotBlank(message = "이름을 입력해주세요.")
    val name: String,

    @field:NotNull(message = "날짜를 입력해주세요.")
    val date: LocalDate,

    @field:NotNull(message = "유효한 일정 타입을 입력해주세요.")
    val type: AssignmentType,

    @field:NotBlank(message = "설명을 입력해주세요.")
    val description: String
)