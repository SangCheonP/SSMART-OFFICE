package org.ssmartoffice.assignmentservice.controller

import org.ssmartoffice.assignmentservice.global.dto.CommonResponse
import org.ssmartoffice.assignmentservice.controller.port.AssignmentService
import org.ssmartoffice.assignmentservice.controller.request.AssignmentRegisterRequest
import org.ssmartoffice.assignmentservice.controller.response.AssignmentInfoResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Positive
import org.springframework.security.core.Authentication
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/v1/assignments")
class AssignmentController(
    val assignmentService: AssignmentService
) {

    @PostMapping
    fun registerAssignment(
        @RequestBody @Valid assignmentRegisterRequest: AssignmentRegisterRequest,
        authentication: Authentication
    ): ResponseEntity<CommonResponse<AssignmentInfoResponse?>> {
        val userId = authentication.principal as Long
        val registeredAssignment = assignmentService.addAssignment(userId, assignmentRegisterRequest)

        return CommonResponse.created(
            data = AssignmentInfoResponse.fromModel(registeredAssignment),
            msg = "일정 등록에 성공하였습니다."
        )
    }

    @GetMapping
    fun getMyDailyAssignments(
        @RequestParam @Pattern(regexp = "^\\d{6}$", message = "month는 YYYYMM 형식의 6자리여야 합니다.") month: String,
        @RequestParam @Pattern(regexp = "^\\d{2}$", message = "day는 DD 형식의 2자리여야 합니다.") day: String,
        authentication: Authentication
    ): ResponseEntity<CommonResponse<List<AssignmentInfoResponse>?>> {
        val userId = authentication.principal as Long
        return getDailyAssignments(userId, month, day, "내 일정 일별 조회에 성공했습니다.")
    }


    @GetMapping("/summary")
    fun getMyMonthlyAssignmentsSummary(
        @RequestParam @Pattern(regexp = "^\\d{6}$", message = "month는 YYYYMM 형식의 6자리여야 합니다.") month: String,
        authentication: Authentication
    ): ResponseEntity<CommonResponse<List<AssignmentInfoResponse>?>> {
        val userId = authentication.principal as Long
        val assignments = assignmentService.findUserAssignmentByDate(userId, month)
        val assignmentSummaries: List<AssignmentInfoResponse> = assignments.map { assignment ->
            AssignmentInfoResponse.fromModel(assignment)
        }
        return CommonResponse.ok(
            data = assignmentSummaries,
            msg = "내 일정 월별 요약 조회에 성공했습니다."
        )
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{userId}")
    fun getEmployeeDailyAssignments(
        @RequestParam @Pattern(regexp = "^\\d{6}$", message = "month는 YYYYMM 형식의 6자리여야 합니다.") month: String,
        @RequestParam @Pattern(regexp = "^\\d{2}$", message = "day는 DD 형식의 2자리여야 합니다.") day: String,
        @Positive @PathVariable userId: Long
    ): ResponseEntity<CommonResponse<List<AssignmentInfoResponse>?>> {
        return getDailyAssignments(userId, month, day, "사원별 일정 일별 조회에 성공했습니다.")
    }

    @PatchMapping("/{assignmentId}")
    fun toggleAssignmentCompletion(
        @Positive @PathVariable assignmentId: Long,
        authentication: Authentication
    ): ResponseEntity<CommonResponse<AssignmentInfoResponse?>> {
        val userId = authentication.principal as Long
        val assignment = assignmentService.toggleAssignmentStatus(userId, assignmentId)
        return CommonResponse.ok(
            data = AssignmentInfoResponse.fromModel(assignment),
            msg = "일정 토글에 성공했습니다."
        )
    }

    private fun getDailyAssignments(
        userId: Long,
        month: String,
        day: String,
        successMessage: String
    ): ResponseEntity<CommonResponse<List<AssignmentInfoResponse>?>> {
        val assignments = assignmentService.findUserAssignmentByDate(userId, month, day)
        val assignmentDetails: List<AssignmentInfoResponse> = assignments.map { assignment ->
            AssignmentInfoResponse.fromModel(assignment)
        }
        return CommonResponse.ok(
            data = assignmentDetails,
            msg = successMessage
        )
    }

}