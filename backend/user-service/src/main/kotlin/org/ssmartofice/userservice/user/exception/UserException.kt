package com.ssmartofice.userservice.user.exception

class UserException(val errorCode: UserErrorCode) : RuntimeException(errorCode.message)
