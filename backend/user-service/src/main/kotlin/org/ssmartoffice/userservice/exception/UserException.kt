package org.ssmartoffice.userservice.exception

class UserException(val errorCode: UserErrorCode) : RuntimeException(errorCode.message)
