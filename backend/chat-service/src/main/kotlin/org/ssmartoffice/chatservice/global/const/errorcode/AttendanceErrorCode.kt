package org.ssmartoffice.chatservice.global.const.errorcode

import org.springframework.http.HttpStatus

enum class MessageErrorCode(override val httpStatus: HttpStatus, override val message: String) : ErrorCode {
}