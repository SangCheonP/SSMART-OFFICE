package org.ssmartoffice.authenticationservice.user.exception

class UserException(val errorCode: UserErrorCode)
    : RuntimeException(errorCode.message)
