package org.ssmartoffice.attendanceservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.ssmartoffice.attendanceservice.controller.port.AttendanceService
import org.ssmartoffice.attendanceservice.controller.response.AttendanceResponse
import org.ssmartoffice.attendanceservice.global.dto.CommonResponse

@RestController
@RequestMapping("/api/v1/attendances")
class AttendanceController(val attendanceService: AttendanceService) {

    @PostMapping
    fun createAttendance(authentication: Authentication) :ResponseEntity<CommonResponse<Any>> {

        val userEmail :String = authentication.principal as String

        val response :AttendanceResponse  = attendanceService.createAttendance(userEmail)
        return CommonResponse.ok("출퇴근이 완료되었습니다.", response)
    }

    @GetMapping("/me")
    fun getMyAttendanceInfo(
        authentication: Authentication,
        @RequestParam month: String,
        @RequestParam day: String?) :ResponseEntity<CommonResponse<Any>> {

        val userEmail: String = authentication.principal as String
        val attendances : List<AttendanceResponse> = attendanceService.getAttendanceByDate(userEmail, month, day)
        return CommonResponse.ok(
            msg = "내 출근 정보 조회에 성공했습니다.",
            data = attendances)
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getUserAttendanceInfo (
        @PathVariable userId: Long,
        @RequestParam month: String,
        @RequestParam day: String?): ResponseEntity<CommonResponse<Any>> {
        val attendances : List<AttendanceResponse> = attendanceService.getAttendanceByDate(userId, month, day)
        return CommonResponse.ok(
            msg = "유저아이디: ${userId}의 출근 정보 조회에 성공했습니다.",
            data = attendances)
    }
}