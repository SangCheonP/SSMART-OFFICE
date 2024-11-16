package org.ssmartoffice.chatservice.global.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.ssmartoffice.chatservice.global.const.successcode.SuccessCode

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CommonResponse<T>(
    val status: Int,
    val msg: String,
    val data: T? = null
) {
    companion object {

        fun <T> ok(msg: String, data: T? = null): ResponseEntity<CommonResponse<T>> {
            return ResponseEntity(
                CommonResponse(
                    status = SuccessCode.OK.getValue(),
                    msg = msg,
                    data = data
                ),
                HttpStatus.OK
            )
        }

        fun <T> created(msg: String, data: T? = null): ResponseEntity<CommonResponse<T>> {
            return ResponseEntity(
                CommonResponse(
                    status = SuccessCode.CREATED.getValue(),
                    msg = msg,
                    data = data
                ),
                HttpStatus.CREATED
            )
        }
    }
}