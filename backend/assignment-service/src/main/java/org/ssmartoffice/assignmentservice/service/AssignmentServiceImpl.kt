package org.ssmartoffice.userservice.service

import org.ssmartoffice.userservice.controller.port.AssignmentService
import org.ssmartoffice.userservice.domain.Assignment
import org.ssmartoffice.userservice.global.exception.AssignmentException
import org.ssmartoffice.userservice.service.port.AssignmentRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.ssmartoffice.assignmentservice.controller.request.AssignmentRegisterRequest
import org.ssmartoffice.userservice.controller.request.*
import org.ssmartoffice.userservice.global.const.errorcode.AssignmentErrorCode

@Service
class AssignmentServiceImpl(
    val passwordEncoder: BCryptPasswordEncoder,
    val assignmentRepository: AssignmentRepository,
) : AssignmentService {

    override fun addAssignment(assignmentRegisterRequest: AssignmentRegisterRequest): Assignment {
        try {
            val assignment = Assignment.fromRequest(assignmentRegisterRequest)
            assignment.encodePassword(passwordEncoder)
            return assignmentRepository.save(assignment)
        } catch (ex: DataIntegrityViolationException) {
            throw AssignmentException(AssignmentErrorCode.DUPLICATED_VALUE)
        }
    }

    override fun findUserByUserId(userId: Long): Assignment {
        try {
            return assignmentRepository.findById(userId)
                ?: throw AssignmentException(AssignmentErrorCode.USER_NOT_FOUND)
        } catch (ex: EmptyResultDataAccessException) {
            throw AssignmentException(AssignmentErrorCode.USER_NOT_FOUND)
        }
    }

    override fun findByUserEmail(userEmail: String): Assignment {
        try {
            return assignmentRepository.findByEmail(userEmail)
                ?: throw AssignmentException(AssignmentErrorCode.USER_NOT_FOUND)
        } catch (ex: EmptyResultDataAccessException) {
            throw AssignmentException(AssignmentErrorCode.USER_NOT_FOUND)
        }
    }

    override fun getAllUsersByPage(pageable: Pageable): Page<Assignment> {
        return assignmentRepository.findAll(pageable)
    }

    override fun updateUser(userId: Long, userUpdateRequest: UserUpdateRequest): Assignment {
        val assignment: Assignment = findUserByUserId(userId)
        assignment.update(
            email = userUpdateRequest.email,
            password = userUpdateRequest.password?.let { passwordEncoder.encode(it) },
            name = userUpdateRequest.name,
            position = userUpdateRequest.position,
            duty = userUpdateRequest.duty,
            profileImageUrl = userUpdateRequest.profileImageUrl,
            phoneNumber = userUpdateRequest.phoneNumber
        )
        return assignmentRepository.save(assignment)
    }

    override fun updatePassword(userId: Long, passwordUpdateRequest: PasswordUpdateRequest) {
        val assignment: Assignment = findUserByUserId(userId)
        assignment.updatePassword(
            oldPassword = passwordUpdateRequest.oldPassword,
            newPassword = passwordUpdateRequest.newPassword,
            encoder = passwordEncoder
        )
        assignmentRepository.save(assignment)
    }

    override fun authenticateUser(userLoginRequest: UserLoginRequest): Assignment {
        val assignment: Assignment = findByUserEmail(userLoginRequest.email)
        assignment.validatePassword(passwordEncoder, userLoginRequest.password)
        return assignment
    }

    override fun findAllByIds(userIds: List<Long>): List<Assignment> {
        return assignmentRepository.findAllByIdIn(userIds)
    }
}