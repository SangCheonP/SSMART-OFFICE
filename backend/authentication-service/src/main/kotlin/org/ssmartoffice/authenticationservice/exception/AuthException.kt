package org.ssmartoffice.authenticationservice.exception

class AuthException(val errorCode: AuthErrorCode)
    : RuntimeException(errorCode.message)
