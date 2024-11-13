package org.ssmartoffice.nfctokenservice.global.exception

import org.ssmartoffice.nfctokenservice.global.const.errorcode.ErrorCode

class RestApiException(val errorCode: ErrorCode) : RuntimeException() {

    companion object {
        private const val serialVersionUID = 8747231388755467240L
    }
}
