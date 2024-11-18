package org.ssmartoffice.seatservice.service

import feign.FeignException
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.ssmartoffice.seatservice.client.UserServiceClient
import org.ssmartoffice.seatservice.controller.port.SeatService
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

    override fun changeSeatStatus(seat: Seat, requestUser: User?, requestStatus: SeatStatus) {
        val userId = requestUser?.userId
        validateSeatChange(seat, userId, requestStatus)
        seatMapper.updateSeat(seat, userId, requestStatus)
        seatRepository.save(seat)
    }

    override fun getSeatStatus(seatId: Long): Seat {
        try {
            return seatRepository.findById(seatId)
        } catch (ex: NoSuchElementException) {
            throw SeatException(SeatErrorCode.SEAT_NOT_FOUND)
        }
    }

    override fun getUserInfo(userId: Long): User? {
        return try {
            val seatUser = userServiceClient.searchUserById(userId).body?.data
            if (seatUser != null) {
                userMapper.toUser(seatUser)
            } else {
                throw SeatException(SeatErrorCode.USER_NOT_FOUND)
            }
        } catch (ex: FeignException.NotFound) {
            throw SeatException(SeatErrorCode.USER_NOT_FOUND)
        } catch (ex: FeignException) {
            throw SeatException(SeatErrorCode.CONNECTION_FAIL)
        }
    }

    override fun findSeatByUserId(userId: Long): Seat? {
        return seatRepository.findByUserId(userId)
    }

    private fun validateSeatChange(seat: Seat, userId: Long?, requestStatus: SeatStatus) {
        if (isOccupiedByAnotherUser(seat, userId, requestStatus)) {
            throw SeatException(SeatErrorCode.OCCUPIED_BY_ANOTHER_USER)
        }
        if (isDuplicateCheckIn(userId, seat.id, requestStatus)) {
            throw SeatException(SeatErrorCode.DUPLICATE_STATUS)
        }
        if (isAttemptToActivateUnavailableSeat(seat, requestStatus)) {
            throw SeatException(SeatErrorCode.SEAT_UNAVAILABLE)
        }
        if (isInvalidNotOccupiedChange(seat, requestStatus)) {
            throw SeatException(SeatErrorCode.ONLY_INUSE)
        }
        if (isInvalidVacantChange(seat, requestStatus)) {
            throw SeatException(SeatErrorCode.ONLY_ACTIVE)
        }
        if (isAttemptToMakeUnavailableWhileActive(seat, requestStatus)) {
            throw SeatException(SeatErrorCode.ONLY_VACANT)
        }
    }

    private fun isOccupiedByAnotherUser(seat: Seat, userId: Long?, requestStatus: SeatStatus): Boolean {
        // 다른 사용자가 점유 중인 좌석을 변경하려는 시도를 방지
        return userId != null && requestStatus.isActive() && seat.isOccupiedByAnotherUser(userId)
    }

    private fun isDuplicateCheckIn(userId: Long?, seatId: Long, requestStatus: SeatStatus): Boolean {
        // 다른 좌석을 이미 사용 중인 경우 중복 체크인 방지
        return userId != null && requestStatus.isActive() && seatRepository.existsByUserIdAndIdNot(userId, seatId)
    }

    private fun isAttemptToActivateUnavailableSeat(seat: Seat, requestStatus: SeatStatus): Boolean {
        // 좌석이 사용 금지 상태일 경우 활성화 상태로 변경 금지
        return requestStatus.isActive() && seat.isUnavailableToUse(requestStatus)
    }

    private fun isInvalidNotOccupiedChange(seat: Seat, requestStatus: SeatStatus): Boolean {
        // 자리 비움은 사용 중인 상태일 때만 가능
        return requestStatus.isNotOccupied() && !seat.isInUse()
    }

    private fun isInvalidVacantChange(seat: Seat, requestStatus: SeatStatus): Boolean {
        // 퇴근은 사용 중 || 자리 비움 상태일 때 가능, 사용 불가 좌석은 사용 가능한 상태로 만들기 가능
        return requestStatus.isVacant() && !seat.status.isActive() && !seat.status.isUnavailable()
    }

    private fun isAttemptToMakeUnavailableWhileActive(seat: Seat, requestStatus: SeatStatus): Boolean {
        //사용자가 사용 중일 때는 사용 불가로 막을 수 없음
        return seat.status.isActive() && requestStatus.isUnavailable()
    }

}