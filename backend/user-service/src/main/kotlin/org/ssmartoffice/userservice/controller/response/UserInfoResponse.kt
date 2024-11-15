package org.ssmartoffice.userservice.controller.response

import org.ssmartoffice.userservice.domain.Role
import org.ssmartoffice.userservice.domain.UserStatus

class UserInfoResponse(
    val userId: Long = 0,
    val employeeNumber: String,
    val email: String,
    val name: String,
    val position: String,
    val duty: String,
    val profileImageUrl: String,
    var role: Role,
    val status: UserStatus,
    val phoneNumber: String?
)