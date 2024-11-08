package org.ssmartoffice.authenticationservice.exception

class UserException(val errorCode: UserErrorCode)
    : RuntimeException(errorCode.message)
