package org.ssmartoffice.assignmentservice.global.exception

import org.ssmartoffice.assignmentservice.global.const.errorcode.AssignmentErrorCode

class AssignmentException(val errorCode: AssignmentErrorCode) : RuntimeException(errorCode.message)
