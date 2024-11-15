package org.ssmartoffice.assignmentservice.global.const.errorcode

import lombok.Getter
import org.springframework.http.HttpStatus

@Getter
enum class AssignmentErrorCode(override val httpStatus: HttpStatus, override val message: String) : ErrorCode {
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "해당 api에 접근 권한이 없습니다."),
    ACCESS_DENIED_ASSIGNMENT(HttpStatus.UNAUTHORIZED, "해당 일정에 접근 권한이 없습니다."),
    NOT_MATCHED_REDIRECT_URI(HttpStatus.BAD_REQUEST, "Redirect URI가 맞지 않습니다."),
    ACCESS_ADMIN_DENIED(HttpStatus.FORBIDDEN, "관리자만 접근 가능한 api입니다."),
    DUPLICATED_VALUE(HttpStatus.BAD_REQUEST, "중복된 이메일 또는 사원번호 입니다."),
    INVALID_OLD_PASSWORD(HttpStatus.CONFLICT, "비밀번호가 일치하지 않습니다."),
    DUPLICATE_PASSWORD(HttpStatus.CONFLICT, "기존 비밀번호와 동일합니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    NOT_FOUND_ASSIGNMENT(HttpStatus.NOT_FOUND, "일정을 찾을 수 없습니다."),
}