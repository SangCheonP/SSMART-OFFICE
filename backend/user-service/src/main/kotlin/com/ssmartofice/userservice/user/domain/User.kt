package com.ssmartofice.userservice.user.domain

import com.ssmartofice.userservice.user.controller.request.UserRegisterRequest
import com.ssmartofice.userservice.user.exception.UserException
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import com.ssmartofice.userservice.user.exception.UserErrorCode
import org.springframework.security.crypto.password.PasswordEncoder
import kotlin.random.Random

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
    var role: Role = Role.USER,
    val employeeNumber: String = generateEmployeeNumber(),
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
        profileImageUrl: String? = null
    ) {
        email?.let { this.email = it }
        password?.let { this.password = it }
        name?.let { this.name = it }
        position?.let { this.position = it }
        duty?.let { this.duty = it }
        profileImageUrl?.let { this.profileImageUrl = it }
    }

    fun updatePassword(oldPassword: String, newPassword: String, encoder: PasswordEncoder) {
        if (!encoder.matches(oldPassword, this.password)) {
            throw UserException(UserErrorCode.INVALID_OLD_PASSWORD)
        }
        if (oldPassword == newPassword) {
            throw UserException(UserErrorCode.DUPLICATE_PASSWORD)
        }
        this.password = encoder.encode(newPassword)
    }


    companion object { //TODO: 클래스 분리하고 중복 체크

        private fun generateEmployeeNumber(): String {
            val randomNumber = Random.nextInt(1000000, 9999999)
            return "S$randomNumber"
        }

        fun fromRequest(userRegisterRequest: UserRegisterRequest): User {
            return User(
                email = userRegisterRequest.email,
                password = userRegisterRequest.password,
                name = userRegisterRequest.name,
                position = userRegisterRequest.position,
                duty = userRegisterRequest.duty,
                profileImageUrl = userRegisterRequest.profileImageUrl
            )
        }
    }
}
