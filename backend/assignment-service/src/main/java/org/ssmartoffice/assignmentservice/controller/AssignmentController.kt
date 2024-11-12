package org.ssmartoffice.userservice.controller

import org.ssmartoffice.userservice.global.dto.CommonResponse
import org.ssmartoffice.userservice.controller.port.AssignmentService
import org.ssmartoffice.assignmentservice.controller.request.AssignmentRegisterRequest
import org.ssmartoffice.userservice.controller.response.AssignmentDetailResponse
import jakarta.validation.Valid
import org.springframework.security.core.Authentication
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/assignments")
class AssignmentController(
    val assignmentService: AssignmentService
) {
    
    @PostMapping
    fun registerAssignment(
        @RequestBody @Valid assignmentRegisterRequest: AssignmentRegisterRequest
    ): ResponseEntity<CommonResponse<AssignmentDetailResponse?>> {
        val registeredAssignment = assignmentService.addAssignment(assignmentRegisterRequest)
        return CommonResponse.created(
            data = AssignmentDetailResponse.fromModel(registeredAssignment),
            msg = "일정 등록에 성공하였습니다."
        )
    }

    @GetMapping("/me")
    fun getMyInfo(authentication: Authentication): ResponseEntity<CommonResponse<AssignmentDetailResponse?>> {
        val userId = authentication.principal as Long
//        val user = assignmentService.findUserByUserId(userId)
        return CommonResponse.ok(
//            data = AssignmentDetailResponse.fromModel(user),
            msg = "내 정보 조회에 성공했습니다."
        )
    }

}