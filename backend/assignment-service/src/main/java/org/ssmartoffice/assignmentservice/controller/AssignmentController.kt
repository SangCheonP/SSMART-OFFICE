package org.ssmartoffice.assignmentservice.controller

import org.ssmartoffice.assignmentservice.global.dto.CommonResponse
import org.ssmartoffice.assignmentservice.controller.port.AssignmentService
import org.ssmartoffice.assignmentservice.controller.request.AssignmentRegisterRequest
import org.ssmartoffice.assignmentservice.controller.response.AssignmentDetailResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Pattern
import org.springframework.security.core.Authentication
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.ssmartoffice.assignmentservice.controller.response.AssignmentInfoResponse

@RestController
@RequestMapping("/api/v1/assignments")
class AssignmentController(
    val assignmentService: AssignmentService
) {

    @PostMapping("/me")
    fun registerAssignment(
        @RequestBody @Valid assignmentRegisterRequest: AssignmentRegisterRequest,
        authentication: Authentication
    ): ResponseEntity<CommonResponse<AssignmentDetailResponse?>> {
        val userId = authentication.principal as Long
        val registeredAssignment = assignmentService.addAssignment(userId, assignmentRegisterRequest)

        return CommonResponse.created(
            data = AssignmentDetailResponse.fromModel(registeredAssignment),
            msg = "일정 등록에 성공하였습니다."
        )
    }

    @GetMapping("/me")
    fun getMyDailyAssignments(
        @RequestParam @Pattern(regexp = "^\\d{6}$", message = "month는 YYYYMM 형식의 6자리여야 합니다.") month: String,
        @RequestParam @Pattern(regexp = "^\\d{2}$", message = "day는 DD 형식의 2자리여야 합니다.") day: String,
        authentication: Authentication
    ): ResponseEntity<CommonResponse<List<AssignmentDetailResponse>?>> {
        val userId = authentication.principal as Long
        val assignments = assignmentService.findUserAssignmentByDate(userId, month, day)
        val assignmentDetails: List<AssignmentDetailResponse> = assignments.map { assignment ->
            AssignmentDetailResponse.fromModel(assignment)
        }
        return CommonResponse.ok(
            data = assignmentDetails,
            msg = "내 일정 일별 조회에 성공했습니다."
        )
    }


    @GetMapping("/me/summary")
    fun getMyMonthlyAssignmentsSummary(
        @RequestParam @Pattern(regexp = "^\\d{6}$", message = "month는 YYYYMM 형식의 6자리여야 합니다.") month: String,
        authentication: Authentication
    ): ResponseEntity<CommonResponse<AssignmentDetailResponse?>> {
        val userId = authentication.principal as Long
//        val user = assignmentService.findUserByUserId(userId)
        return CommonResponse.ok(
//            data = AssignmentDetailResponse.fromModel(user),
            msg = "내 일정 월별 요약 조회에 성공했습니다."
        )
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/{userId}")
    fun getEmployeeDailyAssignments(
        @RequestParam @Pattern(regexp = "^\\d{6}$", message = "month는 YYYYMM 형식의 6자리여야 합니다.") month: String,
        @RequestParam @Pattern(regexp = "^\\d{2}$", message = "day는 DD 형식의 2자리여야 합니다.") day: String,
        @PathVariable userId: Long
    ): ResponseEntity<CommonResponse<List<AssignmentDetailResponse>?>> {
        val assignments = assignmentService.findUserAssignmentByDate(userId, month, day)
        val assignmentDetails: List<AssignmentDetailResponse> = assignments.map { assignment ->
            AssignmentDetailResponse.fromModel(assignment)
        }
        return CommonResponse.ok(
            data = assignmentDetails,
            msg = "사원별 일정 일별 조회에 성공했습니다."
        )
    }

    @PutMapping("/{assignmentId}")
    fun toggleAssignment(
        @PathVariable assignmentId: Long,
        authentication: Authentication
    ): ResponseEntity<CommonResponse<AssignmentDetailResponse?>> {
        val userId = authentication.principal as Long
//        val user = assignmentService.findUserByUserId(userId)
        return CommonResponse.ok(
//            data = AssignmentDetailResponse.fromModel(user),
            msg = "일정 토글에 성공했습니다."
        )
    }

}