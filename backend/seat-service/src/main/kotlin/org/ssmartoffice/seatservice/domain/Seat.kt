package org.ssmartoffice.seatservice.domain

import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

data class Seat(
    val id: Long = 0,
    val userId: Long?,
    @field:NotBlank(message = "층 정보가 없습니다.")
    val floor: Int,
    @field:NotBlank(message = "좌석 상태가 없습니다.")
    val status: SeatStatus,
    @field:NotBlank(message = "좌석 정보가 없습니다.")
    val info: String,
    val updatedDateTime: LocalDateTime?
)
