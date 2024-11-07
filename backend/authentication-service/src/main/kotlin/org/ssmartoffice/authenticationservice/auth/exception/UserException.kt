package org.ssmartoffice.authenticationservice.auth.exception

class UserException(val errorCode: UserErrorCode)
    : RuntimeException(errorCode.message)
