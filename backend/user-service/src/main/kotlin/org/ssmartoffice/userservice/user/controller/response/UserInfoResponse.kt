package org.ssmartoffice.userservice.user.controller.response

import org.ssmartoffice.userservice.user.domain.Role
import org.ssmartoffice.userservice.user.domain.User
import org.ssmartoffice.userservice.user.domain.UserStatus

class UserInfoResponse(
    val id: Long = 0,
    val employeeNumber: String,
    val email: String,
    val name: String,
    val position: String,
    val duty: String,
    val profileImageUrl: String,
    var role: Role,
    val status: UserStatus,
    val phoneNumber:String?
) {
    companion object {
        fun fromModel(user: User): UserInfoResponse {
            return UserInfoResponse(
                id = user.id,
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
    }
}
