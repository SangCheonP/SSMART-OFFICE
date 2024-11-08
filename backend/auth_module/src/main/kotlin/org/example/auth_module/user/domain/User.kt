package org.example.auth_module.user.domain

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.example.auth_module.global.auth.domain.Role
import org.springframework.security.crypto.password.PasswordEncoder

class User(
    val id: Int?,
    @field:NotBlank(message = "아이디 입력해주세요.")
    val loginId: String?,
    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    var password: String?,
    var role: Role = Role.USER,
    @field:NotBlank(message = "이름을 입력해주세요.")
    val name: String = "",
    @field:Email(message = "이메일 형식이 올바르지 않습니다.", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @field:NotBlank(message = "이메일을 입력해주세요.")
    val email: String = "",
    refreshToken: String?
) {

    var refreshToken = refreshToken
    private set


    fun encodePassword(encoder: PasswordEncoder) {
        this.password = encoder.encode(password)
    }

    fun updateRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }
}