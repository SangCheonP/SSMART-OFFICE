package org.ssmartoffice.seatservice.service

import org.springframework.stereotype.Service
import org.ssmartoffice.seatservice.client.UserServiceClient
import org.ssmartoffice.seatservice.client.response.SeatUserResponse
import org.ssmartoffice.seatservice.controller.port.SeatService
import org.ssmartoffice.seatservice.domain.Seat
import org.ssmartoffice.seatservice.domain.User
import org.ssmartoffice.seatservice.global.const.errorcode.SeatErrorCode
import org.ssmartoffice.seatservice.global.exception.SeatException
import org.ssmartoffice.seatservice.infratructure.SeatRepositoryImpl

@Service
class SeatServiceImpl(
    private val seatRepository: SeatRepositoryImpl,
    val userServiceClient: UserServiceClient,
) : SeatService {

    override fun getSeatsByFloor(floor: Int): List<Seat> {
        return seatRepository.findAllByFloor(floor)
    }

    override fun getUsersAtSeats(seats: List<Seat>): List<User> {
        val userIds = seats.map { it.userId }
        val seatUsers = userServiceClient.searchUsersByIds(userIds).body?.data
        if (seatUsers != null) return seatUsers.map { seatUser ->
            SeatUserResponse.toModel(seatUser)
        }
        throw SeatException(SeatErrorCode.USER_NOT_FOUND)
    }
}