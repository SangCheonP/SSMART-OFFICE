package org.ssmartoffice.userservice.global.exception

import org.ssmartoffice.userservice.global.const.errorcode.AssignmentErrorCode

class AssignmentException(val errorCode: AssignmentErrorCode) : RuntimeException(errorCode.message)
