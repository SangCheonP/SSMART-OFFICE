package org.ssmartoffice.seatservice.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.ssmartoffice.seatservice.client.UserServiceClient
import org.ssmartoffice.seatservice.controller.port.SeatService
import org.ssmartoffice.seatservice.controller.request.SeatUpdateRequest
import org.ssmartoffice.seatservice.domain.Seat
import org.ssmartoffice.seatservice.domain.SeatStatus
import org.ssmartoffice.seatservice.domain.User
import org.ssmartoffice.seatservice.global.const.errorcode.SeatErrorCode
import org.ssmartoffice.seatservice.global.exception.SeatException
import org.ssmartoffice.seatservice.infratructure.SeatRepositoryImpl

private val logger = KotlinLogging.logger {}

@Service
class SeatServiceImpl(
    private val seatRepository: SeatRepositoryImpl,
    val userServiceClient: UserServiceClient,
    val userMapper: UserMapper,
    private val seatMapper: SeatMapper
) : SeatService {

    override fun getSeatsByFloor(floor: Int): List<Seat> {
        return seatRepository.findAllByFloor(floor)
    }

    override fun getUsersAtSeats(seats: List<Seat>): List<User> {
        val userIds = seats.mapNotNull { it.userId }
        if (userIds.isEmpty()) {
            return emptyList()
        }
        val seatUserResponse = userServiceClient.searchUsersByIds(userIds).body?.data
        return seatUserResponse?.map { response ->
            userMapper.toUser(response)
        } ?: emptyList()
    }

    override fun changeSeatStatus(id: Long, seatUpdateRequest: SeatUpdateRequest): Seat {
        try {
            val seat = seatRepository.findById(id)

            // 회원 존재 여부 확인
            val userExists = userServiceClient.existsById(seatUpdateRequest.userId).body?.data ?: false
            if (!userExists) throw SeatException(SeatErrorCode.USER_NOT_FOUND)

            // 좌석이 비활성화 상태일 경우 활성화 상태로 변경 금지
            if (seat.isUnavailableToUse(seatUpdateRequest.status)) {
                throw SeatException(SeatErrorCode.SEAT_UNAVAILABLE)
            }

            // 다른 사용자가 점유 중인 좌석을 변경하려는 시도를 방지
            if (seat.isOccupiedByAnotherUser(seatUpdateRequest.userId)) {
                throw SeatException(SeatErrorCode.OCCUPIED_BY_ANOTHER_USER)
            }

            // 다른 좌석을 이미 사용 중인 경우 중복 체크인 방지
            if (seatUpdateRequest.status.isActive() && isDuplicateCheckIn(seatUpdateRequest.userId)) {
                throw SeatException(SeatErrorCode.DUPLICATE_STATUS)
            }
            seatMapper.updateSeat(seat, seatUpdateRequest)
            seatRepository.save(seat)
            return seat
        } catch (ex: NoSuchElementException) {
            throw SeatException(SeatErrorCode.SEAT_NOT_FOUND)
        }
    }

    override fun getUserAtSeat(seat: Seat): User? {
        val userId = seat.userId ?: return null
        val seatUser = userServiceClient.searchUserById(userId).body?.data
        if (seatUser != null) {
            return userMapper.toUser(seatUser)
        }
        throw SeatException(SeatErrorCode.SEAT_NOT_FOUND)
    }

    private fun isDuplicateCheckIn(userId: Long): Boolean {
        return seatRepository.existsByUserIdAndStatus(userId, SeatStatus.IN_USE) ||
                seatRepository.existsByUserIdAndStatus(userId, SeatStatus.NOT_OCCUPIED)
    }
}