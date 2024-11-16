package org.ssmartoffice.chatservice.global.exception

import org.ssmartoffice.chatservice.global.const.errorcode.ErrorCode


class RestApiException(val errorCode: ErrorCode) : RuntimeException() {

    companion object {
        private const val serialVersionUID = 8747231388755467240L
    }
}
