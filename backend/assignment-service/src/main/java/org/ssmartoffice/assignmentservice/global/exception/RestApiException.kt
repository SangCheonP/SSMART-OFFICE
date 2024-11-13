package org.ssmartoffice.assignmentservice.global.exception

import org.ssmartoffice.assignmentservice.global.const.errorcode.ErrorCode

class RestApiException(val errorCode: ErrorCode) : RuntimeException() {

    companion object {
        private const val serialVersionUID = 8747231388755467240L
    }
}
