package org.example.auth_module.global.exception.errorcode

import lombok.Getter
import org.springframework.http.HttpStatus

@Getter
enum class UserErrorCode(override val httpStatus: HttpStatus, override val message: String) : ErrorCode {
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "저장된 리프레시 토큰이 없습니다."),
    NOT_MATCH_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다."),
    NO_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "해당 토큰이 존재하지 않습니다."),
    EXPIRED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "해당 토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "사용할 수 없는 토큰입니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "해당 api에 접근 권한이 없습니다."),
    NOT_MATCHED_REDIRECT_URI(HttpStatus.BAD_REQUEST, "Redirect URI가 맞지 않습니다."),
}