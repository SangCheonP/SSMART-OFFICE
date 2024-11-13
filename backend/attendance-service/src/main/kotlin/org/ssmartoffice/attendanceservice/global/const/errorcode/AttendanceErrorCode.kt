package org.ssmartoffice.attendanceservice.global.const.errorcode

import org.springframework.http.HttpStatus

enum class AttendanceErrorCode(override val httpStatus: HttpStatus, override val message: String) : ErrorCode {
    ATTENDANCE_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "이미 퇴근 정보가 존재합니다."),
}