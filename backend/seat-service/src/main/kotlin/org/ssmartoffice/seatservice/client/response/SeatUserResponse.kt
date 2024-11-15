package org.ssmartoffice.seatservice.client.response

import org.ssmartoffice.seatservice.domain.User

data class SeatUserResponse(
    val id: Long = 0,
    var name: String,
    var position: String,
    var duty: String,
) {
    companion object {
        fun fromModel(user: User): SeatUserResponse {
            return SeatUserResponse(
                id = user.id,
                name = user.name,
                position = user.position,
                duty = user.duty
            )
        }

        fun toModel(seatUser: SeatUserResponse): User {
            return User(
                id = seatUser.id,
                name = seatUser.name,
                position = seatUser.position,
                duty = seatUser.duty
            )
        }
    }
}
