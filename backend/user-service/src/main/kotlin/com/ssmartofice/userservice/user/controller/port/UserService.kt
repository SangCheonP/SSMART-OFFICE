package com.ssmartofice.userservice.user.controller.port

import com.ssmartofice.userservice.user.domain.User

interface UserService {
    fun addUser(user: User): User
}