package org.ssmartoffice.authenticationservice.global.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.ssmartoffice.authenticationservice.global.const.successcode.SuccessCode
import lombok.Builder
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CommonResponse(
    val status: Int,
    val msg: String,
    val data: Any?=null
) {
    companion object {

        fun ok(msg: String, data: Any): ResponseEntity<CommonResponse> {
            return ResponseEntity(
                CommonResponse(
                    status = SuccessCode.OK.getValue(),
                    msg = msg,
                    data = data
                ),
                HttpStatus.OK
            )
        }

        fun ok(msg: String): ResponseEntity<CommonResponse> {
            return ResponseEntity(
                CommonResponse(
                    status = SuccessCode.OK.getValue(),
                    msg = msg,
                    data = null
                ),
                HttpStatus.OK
            )
        }

        fun created(msg: String, data: Any): ResponseEntity<CommonResponse> {
            return ResponseEntity(
                CommonResponse(
                    status = SuccessCode.CREATED.getValue(),
                    msg = msg,
                    data = data
                ),
                HttpStatus.OK
            )
        }

        fun created(msg: String): ResponseEntity<CommonResponse> {
            return ResponseEntity(
                CommonResponse(
                    status = SuccessCode.CREATED.getValue(),
                    msg = msg,
                    data = null
                ),
                HttpStatus.OK
            )
        }
    }
}