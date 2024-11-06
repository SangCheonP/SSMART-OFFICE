package org.smartoffice.apigateway.exception.errorcode

import org.springframework.http.HttpStatus

enum class JwtErrorCode(override val httpStatus: HttpStatus, override val message: String) : ErrorCode {
    NO_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "해당 토큰이 존재하지 않습니다."),
    EXPIRED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "해당 토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "사용할 수 없는 토큰입니다."),
}