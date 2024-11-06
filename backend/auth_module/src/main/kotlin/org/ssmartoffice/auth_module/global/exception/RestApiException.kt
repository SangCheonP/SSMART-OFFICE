package org.ssmartoffice.auth_module.global.exception

import org.ssmartoffice.auth_module.global.exception.errorcode.ErrorCode

class RestApiException(val errorCode: ErrorCode) : RuntimeException() {

    companion object {
        private const val serialVersionUID = 8747231388755467240L
    }
}
