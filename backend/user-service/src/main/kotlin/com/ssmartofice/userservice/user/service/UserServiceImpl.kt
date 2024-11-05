package com.ssmartofice.userservice.user.service

import com.ssmartofice.userservice.user.controller.port.UserService
import com.ssmartofice.userservice.user.controller.request.PasswordUpdateRequest
import com.ssmartofice.userservice.user.controller.request.UserRegisterRequest
import com.ssmartofice.userservice.user.controller.request.UserUpdateRequest
import com.ssmartofice.userservice.user.domain.User
import com.ssmartofice.userservice.user.exception.UserErrorCode
import com.ssmartofice.userservice.user.exception.UserException
import com.ssmartofice.userservice.user.service.port.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    val passwordEncoder: BCryptPasswordEncoder,
    val userRepository: UserRepository
) : UserService {

    override fun addUser(userRegisterRequest: UserRegisterRequest): User {
        try {
            val user = User.fromRequest(userRegisterRequest)
            user.encodePassword(passwordEncoder)
            return userRepository.save(user)
        } catch (ex: DataIntegrityViolationException) {
            throw UserException(UserErrorCode.DUPLICATED_EMAIL)
        }
    }

    override fun findUserByUserId(userId: Long): User {
        return userRepository.findById(userId)
            ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
    }

    override fun getAllUsersByPage(pageable: Pageable): Page<User> {
        return userRepository.findAll(pageable)
    }

    override fun updateUser(userId: Long, userUpdateRequest: UserUpdateRequest): User {
        val user: User = findUserByUserId(userId)
        user.update(
            email = userUpdateRequest.email,
            password = userUpdateRequest.password?.let { passwordEncoder.encode(it) },
            name = userUpdateRequest.name,
            position = userUpdateRequest.position,
            duty = userUpdateRequest.duty,
            profileImageUrl = userUpdateRequest.profileImageUrl
        )
        return userRepository.save(user)
    }

    override fun updatePassword(userId: Long, passwordUpdateRequest: PasswordUpdateRequest) {
        val user: User = findUserByUserId(userId)
        user.updatePassword(
            oldPassword = passwordUpdateRequest.oldPassword,
            newPassword = passwordUpdateRequest.newPassword,
            encoder = passwordEncoder
        )
        userRepository.save(user)
    }
}