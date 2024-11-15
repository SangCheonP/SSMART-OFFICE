package org.ssmartoffice.userservice.domain

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.security.crypto.password.PasswordEncoder

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

    fun checkPassword(rawPassword: String, encoder: PasswordEncoder): Boolean {
        return encoder.matches(rawPassword, this.password)
    }

    fun changePassword(newPassword: String, encoder: PasswordEncoder) {
        this.password = encoder.encode(newPassword)
    }

}
