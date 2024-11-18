package org.ssmartoffice.pointservice.global.exception

import org.ssmartoffice.pointservice.global.const.errorcode.PointErrorCode


class PointException(val errorCode: PointErrorCode) : RuntimeException(errorCode.message)
