package org.ssmartoffice.seatservice.service

import org.springframework.stereotype.Component
import org.ssmartoffice.seatservice.controller.request.SeatUpdateRequest
import org.ssmartoffice.seatservice.domain.Seat

@Component
class SeatMapper {
    fun updateSeat(seat: Seat, seatUpdateRequest: SeatUpdateRequest) {
        seat.status = seatUpdateRequest.status
        seat.userId = seatUpdateRequest.userId
        if (!seat.status.isActive()) { //활성화 되지 않은 좌석에는 사용자 x
            seat.userId = null
        }
    }
}