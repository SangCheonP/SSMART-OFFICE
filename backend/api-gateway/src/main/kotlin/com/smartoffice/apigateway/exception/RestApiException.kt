package com.smartoffice.apigateway.exception

import com.smartoffice.apigateway.exception.errorcode.ErrorCode

class RestApiException(val errorCode: ErrorCode) : RuntimeException() {

    companion object {
        private const val serialVersionUID = 8747231388755467240L
    }
}
