package org.ssmartoffice.userservice.domain

import org.ssmartoffice.userservice.controller.request.UserRegisterRequest
import org.ssmartoffice.userservice.global.exception.UserException
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.security.crypto.password.PasswordEncoder
import org.ssmartoffice.userservice.global.const.errorcode.UserErrorCode

class User(
    val id: Long = 0,
    @field:Email(message = "이메일 형식이 올바르지 않습니다.", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @field:NotBlank(message = "이메일을 입력해주세요.")
    var email: String,
    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    var password: String,
    @field:NotBlank(message = "이름을 입력해주세요.")
    var name: String,
    @field:NotBlank(message = "직급을 입력해주세요.")
    var position: String,
    @field:NotBlank(message = "직무를 입력해주세요.")
    var duty: String,
    @field:NotBlank(message = "이미지를 입력해주세요.")
    var profileImageUrl: String,
    var phoneNumber: String? = null,
    var role: Role = Role.USER,
    val employeeNumber: String,
    val point: Int = 0,
    val status: UserStatus = UserStatus.OFF_DUTY,
    val deleted: Boolean = false,
) {

    fun encodePassword(encoder: PasswordEncoder) {
        this.password = encoder.encode(password)
    }

    fun update(
        email: String? = null,
        password: String? = null,
        name: String? = null,
        position: String? = null,
        duty: String? = null,
        profileImageUrl: String? = null,
        phoneNumber: String? = null,
    ) {
        email?.let { this.email = it }
        password?.let { this.password = it }
        name?.let { this.name = it }
        position?.let { this.position = it }
        duty?.let { this.duty = it }
        profileImageUrl?.let { this.profileImageUrl = it }
        phoneNumber?.let { this.phoneNumber = it }
    }

    fun updatePassword(oldPassword: String, newPassword: String, encoder: PasswordEncoder) {
        validatePassword(encoder, oldPassword)
        if (oldPassword == newPassword) {
            throw UserException(UserErrorCode.DUPLICATE_PASSWORD)
        }
        this.password = encoder.encode(newPassword)
    }

    fun validatePassword(
        encoder: PasswordEncoder,
        otherPassword: String
    ) {
        if (!encoder.matches(otherPassword, this.password)) {
            throw UserException(UserErrorCode.INVALID_OLD_PASSWORD)
        }
    }

    companion object {

        fun fromRequest(
            userRegisterRequest: UserRegisterRequest
        ): User {
            return User(
                email = userRegisterRequest.email,
                password = userRegisterRequest.password,
                name = userRegisterRequest.name,
                position = userRegisterRequest.position,
                duty = userRegisterRequest.duty,
                profileImageUrl = userRegisterRequest.profileImageUrl,
                employeeNumber = userRegisterRequest.employeeNumber,
                phoneNumber = userRegisterRequest.phoneNumber
            )
        }
    }
}
