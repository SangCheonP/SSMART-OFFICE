package org.ssmartoffice.seatservice.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.ssmartoffice.seatservice.controller.port.SeatService
import org.ssmartoffice.seatservice.controller.request.SeatUpdateRequest
import org.ssmartoffice.seatservice.controller.response.SeatInfoResponse
import org.ssmartoffice.seatservice.global.dto.CommonResponse

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/seats")
class SeatController(
    val seatService: SeatService,
    val seatResponseMapper: SeatResponseMapper
) {

    @GetMapping
    fun getSeatsByFloor(
        @Positive @RequestParam floor: Int
    ): ResponseEntity<CommonResponse<List<SeatInfoResponse>?>> {
        val seats = seatService.getSeatsByFloor(floor)
        val users = seatService.getUsersAtSeats(seats)
        val userMap = users.filter { it.userId != null }.associateBy { it.userId!! }
        val seatInfos = seats.map { seat ->
            val user = seat.userId?.let { userMap[it] }
            seatResponseMapper.toSeatInfoResponse(seat, user)
        }
        return CommonResponse.ok(
            msg = "층별 좌석 조회에 성공했습니다.",
            data = seatInfos
        )
    }

    @PatchMapping("/{seatId}")
    fun changeSeatStatus(
        @Positive @PathVariable seatId: Long,
        @Valid @RequestBody seatUpdateRequest: SeatUpdateRequest,
        authentication: Authentication
    ): ResponseEntity<CommonResponse<SeatInfoResponse?>> {
        val userId = authentication.principal as Long
        val seat = seatService.getSeatStatus(seatId)
        val user = seat.takeIf { seatUpdateRequest.status.isActive() }
            ?.let { runCatching { seatService.getUserInfo(userId) }.getOrNull() }
        seatService.changeSeatStatus(seat, user, seatUpdateRequest.status)
        val seatInfo = seatResponseMapper.toSeatInfoResponse(seat, user)
        return CommonResponse.ok(
            msg = "좌석 상태 업데이트에 성공했습니다.",
            data = seatInfo
        )
    }

    @GetMapping("/users/{userId}")
    fun getSeatByUserId(
        @Positive @PathVariable userId: Long
    ): ResponseEntity<CommonResponse<SeatInfoResponse?>> {
        val seat = seatService.findSeatByUserId(userId)
        val user = seat?.userId?.let { seatService.getUserInfo(it) }

        if(seat == null) {
            return CommonResponse.ok(
                msg = "사용자 좌석 조회에 실패했습니다."
            )
        }

        return CommonResponse.ok(
            msg = "사용자 좌석 조회에 성공했습니다.",
            data = seatResponseMapper.toSeatInfoResponse(seat, user)
        )
    }
}