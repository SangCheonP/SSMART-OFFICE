package com.ssmartofice.userservice.user.exception

import org.example.auth_module.global.exception.errorcode.UserErrorCode

class UserException(val errorCode: UserErrorCode) : RuntimeException(errorCode.message)
