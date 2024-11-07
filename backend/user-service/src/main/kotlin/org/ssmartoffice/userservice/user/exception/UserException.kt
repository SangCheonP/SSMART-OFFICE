package org.ssmartoffice.userservice.user.exception

class UserException(val errorCode: UserErrorCode) : RuntimeException(errorCode.message)
