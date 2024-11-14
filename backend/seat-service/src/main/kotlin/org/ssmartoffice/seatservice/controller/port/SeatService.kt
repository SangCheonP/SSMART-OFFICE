package org.ssmartoffice.seatservice.controller.port

import org.ssmartoffice.seatservice.controller.request.SeatUpdateRequest
import org.ssmartoffice.seatservice.domain.Seat
import org.ssmartoffice.seatservice.domain.User

interface SeatService {
    fun getSeatsByFloor(floor: Int): List<Seat>
    fun getUsersAtSeats(seats: List<Seat>): List<User>
    fun changeSeatStatus(id: Long, seatUpdateRequest: SeatUpdateRequest): Seat
    fun getUserAtSeat(seat: Seat): User?
}