package org.ssmartoffice.pointservice.global.exception

import org.ssmartoffice.pointservice.global.const.errorcode.PointErrorCode


class SeatException(val errorCode: PointErrorCode) : RuntimeException(errorCode.message)
