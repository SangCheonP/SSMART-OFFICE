package org.ssmartoffice.seatservice.service

import org.springframework.stereotype.Component
import org.ssmartoffice.seatservice.domain.Seat
import org.ssmartoffice.seatservice.domain.SeatStatus

@Component
class SeatMapper {

    fun updateSeat(seat: Seat, userId: Long?, requestStatus: SeatStatus) {
        seat.userId = userId
        seat.status= requestStatus
    }

}