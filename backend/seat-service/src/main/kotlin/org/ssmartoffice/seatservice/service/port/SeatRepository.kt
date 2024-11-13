package org.ssmartoffice.seatservice.service.port

import org.springframework.stereotype.Repository
import org.ssmartoffice.seatservice.domain.Seat

@Repository
interface SeatRepository {
    fun findAllByFloor(floor: Int): List<Seat>
}