package com.ssmartofice.userservice.user.controller.port

import com.ssmartofice.userservice.user.controller.request.UserRegisterRequest
import com.ssmartofice.userservice.user.controller.request.UserUpdateRequest
import com.ssmartofice.userservice.user.domain.User
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable

interface UserService {
    fun addUser(userRegisterRequest: UserRegisterRequest): User
    fun findUserByUserId(userId: Long): User
    fun getAllUsersByPage(pageable: Pageable): Page<User>
    fun updateUser(userId: Long, userUpdateRequest: UserUpdateRequest): User
}