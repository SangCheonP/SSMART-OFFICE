package org.ssmartoffice.userservice.controller

import org.springframework.stereotype.Component
import org.ssmartoffice.userservice.controller.response.SeatUserResponse
import org.ssmartoffice.userservice.controller.response.UserInfoResponse
import org.ssmartoffice.userservice.controller.response.UserAuthenticationResponse
import org.ssmartoffice.userservice.domain.User

@Component
class UserResponseMapper {

    fun toUserInfoResponse(user: User): UserInfoResponse {
        return UserInfoResponse(
            userId = user.id,
            email = user.email,
            name = user.name,
            position = user.position,
            duty = user.duty,
            profileImageUrl = user.profileImageUrl,
            role = user.role,
            employeeNumber = user.employeeNumber,
            status = user.status,
            phoneNumber = user.phoneNumber
        )
    }

    fun toSeatUserResponse(user: User): SeatUserResponse {
        return SeatUserResponse(
            userId = user.id,
            name = user.name,
            position = user.position,
            duty = user.duty
        )
    }

    fun toUserAuthenticationResponse(user: User): UserAuthenticationResponse {
        return UserAuthenticationResponse(
            userId = user.id,
            role = user.role,
        )
    }
}