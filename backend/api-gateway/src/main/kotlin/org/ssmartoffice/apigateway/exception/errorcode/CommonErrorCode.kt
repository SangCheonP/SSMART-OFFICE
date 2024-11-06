package org.ssmartoffice.apigateway.exception.errorcode

import org.springframework.http.HttpStatus

enum class CommonErrorCode(override val httpStatus: HttpStatus, override val message: String) : ErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 에러 입니다."),
    ;
}
