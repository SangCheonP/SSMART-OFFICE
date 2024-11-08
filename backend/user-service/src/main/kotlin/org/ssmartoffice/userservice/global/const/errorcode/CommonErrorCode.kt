package org.ssmartoffice.userservice.global.const.errorcode

import org.springframework.http.HttpStatus

enum class CommonErrorCode(override val httpStatus: HttpStatus, override val message: String) : ErrorCode {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 parameter가 포함되었습니다."),
    INVALID_METHOD_ARG(HttpStatus.BAD_REQUEST, "요청 본문에 잘못된 값이 포함되어 있습니다. 필드를 확인해주세요."),
    INVALID_JSON(HttpStatus.BAD_REQUEST, "요청 본문을 읽을 수 없습니다. 올바른 JSON 형식인지 확인해주세요."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 자원입니다."),
    REQUEST_METHOD_NOT_SUPPORTED(HttpStatus.METHOD_NOT_ALLOWED, "해당 요청은 잘못된 Method 요청 입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 에러 입니다."),
    NOT_EXIST_URL(HttpStatus.NOT_FOUND, "존재하지 않는 요청입니다."),
    ;
}
