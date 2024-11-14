package org.ssmartoffice.userservice.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.ssmartoffice.userservice.controller.request.UserRegisterRequest
import org.ssmartoffice.userservice.controller.request.UserUpdateRequest
import org.ssmartoffice.userservice.domain.User

@Component
class UserMapper(private val passwordEncoder: PasswordEncoder) {
    fun toUser(
        userRegisterRequest: UserRegisterRequest
    ): User {
        return User(
            email = userRegisterRequest.email,
            password = passwordEncoder.encode(userRegisterRequest.password),
            name = userRegisterRequest.name,
            position = userRegisterRequest.position,
            duty = userRegisterRequest.duty,
            profileImageUrl = userRegisterRequest.profileImageUrl,
            employeeNumber = userRegisterRequest.employeeNumber,
            phoneNumber = userRegisterRequest.phoneNumber
        )
    }

    fun updateUser(user: User, userUpdateRequest: UserUpdateRequest) {
        userUpdateRequest.name?.let { user.name = it }
        userUpdateRequest.position?.let { user.position = it }
        userUpdateRequest.duty?.let { user.duty = it }
        userUpdateRequest.profileImageUrl?.let { user.profileImageUrl = it }
        userUpdateRequest.phoneNumber?.let { user.phoneNumber = it }
    }
}