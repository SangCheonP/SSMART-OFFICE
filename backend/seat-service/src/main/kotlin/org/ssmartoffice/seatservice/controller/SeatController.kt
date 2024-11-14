package org.ssmartoffice.seatservice.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.http.ResponseEntity
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

    @PatchMapping("/{id}")
    fun changeSeatStatus(
        @Positive @PathVariable id: Long,
        @Valid @RequestBody seatUpdateRequest: SeatUpdateRequest,
    ): ResponseEntity<CommonResponse<SeatInfoResponse?>> {
        val seat = seatService.changeSeatStatus(id, seatUpdateRequest)
        val user = seatService.getUserAtSeat(seat).takeIf { seat.status.isActive() }
        val seatInfo = seatResponseMapper.toSeatInfoResponse(seat, user)
        return CommonResponse.ok(
            msg = "좌석 상태 업데이트에 성공했습니다.",
            data = seatInfo
        )
    }
}