package com.ssmartofice.userservice.global.dto

import com.ssmartofice.userservice.global.const.successcode.SuccessCode
import lombok.Builder

@Builder
data class CommonResponse(
    val status: Int,
    val msg: String,
    val data: Any?
){
    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }

    class Builder {
        private var status: Int = SuccessCode.OK.getValue()
        private var message: String = "Success"
        private var data: Any? = null

        fun status(status: Int) = apply { this.status = status }
        fun message(message: String) = apply { this.message = message }
        fun data(data: Any?) = apply { this.data = data }

        fun build(): CommonResponse {
            return CommonResponse(status, message, data)
        }
    }
}