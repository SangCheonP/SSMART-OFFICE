package org.ssmartoffice.userservice.user.controller.response

import org.ssmartoffice.userservice.user.domain.Role
import org.ssmartoffice.userservice.user.domain.User

class UserLoginResponse(
    val userId: Long = 0,
    var role: Role
) {
    companion object {
        fun fromModel(user: User): UserLoginResponse {
            return UserLoginResponse(
                userId = user.id,
                role = user.role,
            )
        }
    }
}
