package org.ssmartoffice.userservice.controller.port

import org.ssmartoffice.userservice.domain.User
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable
import org.springframework.security.core.Authentication
import org.ssmartoffice.userservice.controller.request.*
import org.ssmartoffice.userservice.controller.response.UserInfoResponse

interface UserService {
    fun addUser(userRegisterRequest: UserRegisterRequest): User
    fun findUserByUserIdWithAuth(userId: Long, authentication: Authentication): User
    fun findUserByUserId(userId: Long): User
    fun findByUserEmail(userEmail: String): User
    fun getAllUsersByPage(pageable: Pageable): Page<User>
    fun updateUser(userId: Long, userUpdateRequest: UserUpdateRequest): User
    fun updatePassword(userId: Long, passwordUpdateRequest: PasswordUpdateRequest)
    fun authenticateUser(userLoginRequest: UserLoginRequest): User
    fun findAllByIds(userIds: List<Long>): List<User>
    fun existsById(userId: Long): Boolean
    fun findUser(keyword: String, pageable: Pageable): Page<UserInfoResponse>?
}