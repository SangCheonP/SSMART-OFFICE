package org.ssmartoffice.seatservice.controller.response

import org.ssmartoffice.seatservice.domain.SeatStatus
import java.time.LocalDateTime

data class SeatInfoResponse(
    val id: Long = 0,
    val info: String,
    val status: SeatStatus,
    val userId: Long?,
    val userName: String?,
    val userPosition: String?,
    val userDuty: String?,
    val lastUpdatedDateTime: LocalDateTime?
)