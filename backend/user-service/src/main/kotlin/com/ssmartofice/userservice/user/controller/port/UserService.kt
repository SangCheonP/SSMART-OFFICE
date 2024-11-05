package com.ssmartofice.userservice.user.controller.port

import com.ssmartofice.userservice.user.domain.User
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable

interface UserService {
    fun addUser(user: User): User
    fun findUserByUserId(userId: Long): User
    fun getAllUsersByPage(pageable: Pageable): Page<User>
}