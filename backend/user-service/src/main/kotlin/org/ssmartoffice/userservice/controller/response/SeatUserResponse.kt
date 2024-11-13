package org.ssmartoffice.userservice.controller.response

import org.ssmartoffice.userservice.domain.User

data class SeatUserResponse(
    val id: Long = 0,
    var name: String,
    var position: String,
    var duty: String,
){
    companion object {
        fun fromModel(user: User): SeatUserResponse {
            return SeatUserResponse(
                id = user.id,
                name = user.name,
                position = user.position,
                duty = user.duty
            )
        }
    }
}
