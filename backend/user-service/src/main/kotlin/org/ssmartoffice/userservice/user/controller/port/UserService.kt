package org.ssmartoffice.userservice.user.controller.port

import org.ssmartoffice.userservice.user.controller.request.PasswordUpdateRequest
import org.ssmartoffice.userservice.user.controller.request.UserRegisterRequest
import org.ssmartoffice.userservice.user.controller.request.UserUpdateRequest
import org.ssmartoffice.userservice.user.domain.User
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable
import org.ssmartoffice.userservice.user.controller.request.UserLoginRequest

interface UserService {
    fun addUser(userRegisterRequest: UserRegisterRequest): User
    fun findUserByUserId(userId: Long): User
    fun findByUserEmail(userEmail: String): User
    fun getAllUsersByPage(pageable: Pageable): Page<User>
    fun updateUser(userId: Long, userUpdateRequest: UserUpdateRequest): User
    fun updatePassword(userId: Long, passwordUpdateRequest: PasswordUpdateRequest)
    fun authenticateUser(request: UserLoginRequest): User
}