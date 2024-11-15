package org.ssmartoffice.pointservice.global.const.errorcode

import lombok.Getter
import org.springframework.http.HttpStatus

@Getter
enum class PointErrorCode(override val httpStatus: HttpStatus, override val message: String) : ErrorCode {
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "해당 api에 접근 권한이 없습니다."),
    ACCESS_ADMIN_DENIED(HttpStatus.FORBIDDEN, "관리자만 접근 가능한 api입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    BALANCE_NOT_ENOUGH(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
}