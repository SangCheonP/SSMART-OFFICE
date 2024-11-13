package org.ssmartoffice.pointservice.global.exception

import org.ssmartoffice.pointservice.global.const.errorcode.ErrorCode

class RestApiException(val errorCode: ErrorCode) : RuntimeException() {

    companion object {
        private const val serialVersionUID = 8747231388755467240L
    }
}
