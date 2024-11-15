package org.ssmartoffice.seatservice.global.exception

import org.ssmartoffice.seatservice.global.const.errorcode.SeatErrorCode


class SeatException(val errorCode: SeatErrorCode) : RuntimeException(errorCode.message)
