package org.ssmartoffice.fileservice.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.ssmartoffice.fileservice.const.successcode.SuccessCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

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