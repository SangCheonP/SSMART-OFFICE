package org.ssmartoffice.seatservice.service

import org.springframework.stereotype.Component
import org.ssmartoffice.seatservice.client.response.SeatUserResponse
import org.ssmartoffice.seatservice.domain.User

@Component
class UserMapper {
    fun toUser(
        seatUserResponse: SeatUserResponse
    ): User {
        return User(
            userId = seatUserResponse.userId,
            name = seatUserResponse.name,
            position = seatUserResponse.position,
            duty = seatUserResponse.duty
        )
    }
}