package org.ssmartoffice.seatservice.controller.port

import org.ssmartoffice.seatservice.domain.Seat
import org.ssmartoffice.seatservice.domain.SeatStatus
import org.ssmartoffice.seatservice.domain.User

interface SeatService {
    fun getSeatsByFloor(floor: Int): List<Seat>
    fun getUsersAtSeats(seats: List<Seat>): List<User>
    fun getSeatStatus(seatId: Long): Seat
    fun getUserInfo(userId: Long): User?
    fun changeSeatStatus(seat: Seat, requestUser: User?, requestStatus: SeatStatus)
    fun findSeatByUserId(userId: Long): Seat?
}