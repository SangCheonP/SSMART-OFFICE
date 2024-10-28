package org.example.auth_module.global.exception

import lombok.Getter
import lombok.RequiredArgsConstructor
import org.example.auth_module.global.exception.errorcode.ErrorCode

@Getter
@RequiredArgsConstructor
class RestApiException(val errorCode: ErrorCode) : RuntimeException() {

    companion object {
        private const val serialVersionUID = 8747231388755467240L
    }
}
