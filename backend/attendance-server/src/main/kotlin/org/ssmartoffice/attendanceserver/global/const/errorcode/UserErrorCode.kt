package org.ssmartoffice.attendanceserver.global.const.errorcode

import org.springframework.http.HttpStatus

enum class UserErrorCode(override val httpStatus: HttpStatus, override val message: String) : ErrorCode {
    ACCESS_ADMIN_DENIED(HttpStatus.UNAUTHORIZED, "해당 api에 접근 권한이 없습니다."),
}