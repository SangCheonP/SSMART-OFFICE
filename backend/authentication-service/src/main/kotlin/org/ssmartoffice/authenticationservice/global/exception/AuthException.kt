package org.ssmartoffice.authenticationservice.global.exception

import org.ssmartoffice.authenticationservice.global.const.errorcode.AuthErrorCode

class AuthException(val errorCode: AuthErrorCode)
    : RuntimeException(errorCode.message)
