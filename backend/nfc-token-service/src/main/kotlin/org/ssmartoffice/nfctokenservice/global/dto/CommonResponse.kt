package org.ssmartoffice.nfctokenservice.global.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.ssmartoffice.nfctokenservice.global.const.successcode.SuccessCode

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CommonResponse(
    val status: Int,
    val msg: String,
    val data: Any?
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