package org.ssmartoffice.authenticationservice.global.client.response

import org.ssmartoffice.authenticationservice.auth.domain.User

class UserLoginResponse(
    val userId: Long? = 0,
    var role: String
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
