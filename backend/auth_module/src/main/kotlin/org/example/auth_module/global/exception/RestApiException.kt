package org.example.auth_module.global.exception

import org.example.auth_module.global.exception.errorcode.ErrorCode

class RestApiException(val errorCode: ErrorCode) : RuntimeException() {

    companion object {
        private const val serialVersionUID = 8747231388755467240L
    }
}
