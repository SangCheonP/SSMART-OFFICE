package com.ssmartofice.userservice.user.service

import com.ssmartofice.userservice.user.controller.port.UserService
import com.ssmartofice.userservice.user.domain.User
import com.ssmartofice.userservice.user.service.port.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    val passwordEncoder: BCryptPasswordEncoder,
    val userRepository: UserRepository
) : UserService {

    override fun addUser(user: User): User {
        user.encodePassword(passwordEncoder)
        return userRepository.save(user)
    }

    override fun findUserByUserId(userId: Long): User {
        return userRepository.findById(userId)
    }
}