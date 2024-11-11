package org.ssmartoffice.userservice.global.exception

import org.ssmartoffice.userservice.global.const.errorcode.UserErrorCode

class UserException(val errorCode: UserErrorCode) : RuntimeException(errorCode.message)
