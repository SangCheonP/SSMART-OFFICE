package org.ssmartoffice.seatservice.controller.request

import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.NotNull
import org.ssmartoffice.seatservice.domain.SeatStatus

data class SeatUpdateRequest(
    @field:NotNull(message = "회원 아이디를 입력해주세요.")
    @field:Positive(message = "회원 아이디는 양수여야 합니다.")
    val userId: Long = 0,

    @field:NotNull(message = "좌석 상태를 입력해주세요.")
    val status: SeatStatus,
)
