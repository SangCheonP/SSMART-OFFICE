package org.ssmartoffice.fileservice.const.successcode

import org.springframework.http.HttpStatus

enum class SuccessCode(private val httpStatus: HttpStatus) {
    OK(HttpStatus.OK),
    CREATED(HttpStatus.CREATED);

    fun getValue(): Int {
        return httpStatus.value()
    }
}