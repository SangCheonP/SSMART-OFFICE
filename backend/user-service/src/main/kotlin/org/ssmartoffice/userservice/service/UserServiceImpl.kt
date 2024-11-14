package org.ssmartoffice.userservice.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.ssmartoffice.userservice.controller.port.UserService
import org.ssmartoffice.userservice.domain.User
import org.ssmartoffice.userservice.global.exception.UserException
import org.ssmartoffice.userservice.service.port.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.ssmartoffice.userservice.controller.request.*
import org.ssmartoffice.userservice.domain.Role
import org.ssmartoffice.userservice.global.const.errorcode.UserErrorCode

private val logger = KotlinLogging.logger {}

@Service
class UserServiceImpl(
    val passwordEncoder: BCryptPasswordEncoder,
    val userRepository: UserRepository,
    private val userMapper: UserMapper,
) : UserService {

    override fun addUser(userRegisterRequest: UserRegisterRequest): User {
        try {
            val user = userMapper.toUser(userRegisterRequest)
            return userRepository.save(user)
        } catch (ex: DataIntegrityViolationException) {
            throw UserException(UserErrorCode.DUPLICATED_VALUE)
        }
    }

    override fun findUserByUserId(userId: Long): User {
        try {
            return userRepository.findById(userId)
                ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        } catch (ex: EmptyResultDataAccessException) {
            throw UserException(UserErrorCode.USER_NOT_FOUND)
        }
    }

    override fun findByUserEmail(userEmail: String): User {
        try {
            return userRepository.findByEmail(userEmail)
                ?: throw UserException(UserErrorCode.USER_NOT_FOUND)
        } catch (ex: EmptyResultDataAccessException) {
            throw UserException(UserErrorCode.USER_NOT_FOUND)
        }
    }

    override fun getAllUsersByPage(pageable: Pageable): Page<User> {
        return userRepository.findByRoleNot(Role.ADMIN, pageable)
    }

    override fun updateUser(userId: Long, userUpdateRequest: UserUpdateRequest): User {
        val user: User = findUserByUserId(userId)
        userMapper.updateUser(user, userUpdateRequest)
        return userRepository.save(user)
    }

    override fun updatePassword(userId: Long, passwordUpdateRequest: PasswordUpdateRequest) {
        val user: User = findUserByUserId(userId)
        if (!user.checkPassword(passwordUpdateRequest.oldPassword, passwordEncoder)) {
            throw UserException(UserErrorCode.INVALID_PASSWORD)
        }
        if (passwordUpdateRequest.isSamePassword()) {
            throw UserException(UserErrorCode.DUPLICATE_PASSWORD)
        }
        user.changePassword(passwordUpdateRequest.newPassword, passwordEncoder)
        userRepository.save(user)
    }

    override fun authenticateUser(userLoginRequest: UserLoginRequest): User {
        val user: User = findByUserEmail(userLoginRequest.email)
        if (!user.checkPassword(userLoginRequest.password, passwordEncoder)) {
            throw UserException(UserErrorCode.INVALID_PASSWORD)
        }
        return user
    }

    override fun findAllByIds(userIds: List<Long>): List<User> {
        return userRepository.findAllByIdIn(userIds)
    }
}