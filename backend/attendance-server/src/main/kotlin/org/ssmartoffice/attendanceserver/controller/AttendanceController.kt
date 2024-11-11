package org.ssmartoffice.attendanceserver.controller

import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.ssmartoffice.attendanceserver.controller.port.AttendanceService
import org.ssmartoffice.attendanceserver.global.dto.CommonResponse

@RestController
@RequestMapping("/api/v1/attendances")
class AttendanceController(val attendanceService: AttendanceService) {

    @PostMapping
    fun createAttendance(authentication: Authentication) :ResponseEntity<CommonResponse> {

        val userId: Long = authentication.principal as Long

        attendanceService.createAttendance(userId)
        return CommonResponse.ok("출퇴근이 완료되었습니다.")
    }

    @GetMapping("/me")
    fun getMyAttendanceInfo(
        @RequestParam month: String,
        @RequestParam day: String?) :ResponseEntity<CommonResponse> {
        return CommonResponse.ok("내 출근 정보 조회에 성공했습니다.")
    }

    @GetMapping("/user/{userId}")
    fun getUserAttendanceInfo (
        @PathVariable userId: String,
        @RequestParam month: String,
        @RequestParam day: String): ResponseEntity<CommonResponse> {
        return CommonResponse.ok("유저아이디: ${userId}의 출근 정보 조회에 성공했습니다.",)
    }
}