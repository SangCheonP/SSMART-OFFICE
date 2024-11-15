package org.ssmartoffice.seatservice.controller.request

import jakarta.validation.constraints.NotNull
import org.ssmartoffice.seatservice.domain.SeatStatus

data class SeatUpdateRequest(

    @field:NotNull(message = "좌석 상태를 입력해주세요.")
    val status: SeatStatus,
)
