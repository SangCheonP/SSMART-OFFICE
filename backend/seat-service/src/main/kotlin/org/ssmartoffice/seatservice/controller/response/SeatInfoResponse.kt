package org.ssmartoffice.seatservice.controller.response

import org.ssmartoffice.seatservice.domain.Seat
import org.ssmartoffice.seatservice.domain.SeatStatus
import org.ssmartoffice.seatservice.domain.User
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
) {
    companion object {
        fun fromModel(seat: Seat, user: User?): SeatInfoResponse {
            return SeatInfoResponse(
                id = seat.id,
                info = seat.info,
                status = seat.status,
                userId = user?.id,
                userName = user?.name,
                userPosition = user?.position,
                userDuty = user?.duty,
                lastUpdatedDateTime = seat.updatedDateTime
            )
        }
    }
}
