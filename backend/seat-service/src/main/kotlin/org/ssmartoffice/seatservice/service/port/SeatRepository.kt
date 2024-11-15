package org.ssmartoffice.seatservice.service.port

import org.springframework.stereotype.Repository
import org.ssmartoffice.seatservice.domain.Seat
import org.ssmartoffice.seatservice.domain.SeatStatus

@Repository
interface SeatRepository {
    fun findAllByFloor(floor: Int): List<Seat>
    fun findById(id: Long): Seat
    fun save(seat: Seat)
    fun existsByUserIdAndIdNot(userId: Long, id: Long): Boolean
}