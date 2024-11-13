package org.ssmartoffice.seatservice.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.ssmartoffice.seatservice.controller.port.SeatService
import org.ssmartoffice.seatservice.controller.response.SeatInfoResponse
import org.ssmartoffice.seatservice.global.dto.CommonResponse

@RestController
@RequestMapping("/api/v1/seats")
class SeatController(
    val seatService: SeatService
) {

    @GetMapping
    fun getSeatsByFloor(
        @RequestParam floor: Int
    ): ResponseEntity<CommonResponse<List<SeatInfoResponse>?>> {
        val seats = seatService.getSeatsByFloor(floor)
        val users = seatService.getUsersAtSeats(seats)
        val userMap = users.associateBy { it.id }
        val seatInfos = seats.map { seat ->
            val user = userMap[seat.userId]
            SeatInfoResponse.fromModel(seat, user)
        }
        return CommonResponse.ok(
            msg = "층별 좌석 조회에 성공했습니다.",
            data = seatInfos
        )
    }
}