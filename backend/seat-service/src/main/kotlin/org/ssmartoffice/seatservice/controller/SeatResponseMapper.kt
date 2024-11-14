package org.ssmartoffice.seatservice.controller

import org.springframework.stereotype.Component
import org.ssmartoffice.seatservice.controller.response.SeatInfoResponse
import org.ssmartoffice.seatservice.domain.Seat
import org.ssmartoffice.seatservice.domain.User

@Component
class SeatResponseMapper {

    fun toSeatInfoResponse(seat: Seat, user: User?): SeatInfoResponse {
        return SeatInfoResponse(
            id = seat.id,
            info = seat.info,
            status = seat.status,
            userId = seat.userId,
            userName = user?.name,
            userPosition = user?.position,
            userDuty = user?.duty,
            lastUpdatedDateTime = seat.updatedDateTime
        )
    }

}
