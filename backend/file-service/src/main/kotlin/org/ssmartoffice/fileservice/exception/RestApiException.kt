package org.ssmartoffice.fileservice.exception

import org.ssmartoffice.fileservice.const.errorcode.ErrorCode

class RestApiException(val errorCode: ErrorCode) : RuntimeException() {

    companion object {
        private const val serialVersionUID = 8747231388755467240L
    }
}
