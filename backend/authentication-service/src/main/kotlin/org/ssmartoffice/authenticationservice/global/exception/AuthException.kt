package org.ssmartoffice.authenticationservice.global.exception

import org.springframework.security.core.AuthenticationException
import org.ssmartoffice.authenticationservice.global.const.errorcode.AuthErrorCode

class AuthException(val errorCode: AuthErrorCode)
    : AuthenticationException(errorCode.message)
