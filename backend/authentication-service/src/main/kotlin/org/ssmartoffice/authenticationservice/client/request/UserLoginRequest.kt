package org.ssmartoffice.authenticationservice.client.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class UserLoginRequest(
    @field:Email(message = "이메일 형식이 올바르지 않습니다.", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @field:NotBlank(message = "이메일을 입력해주세요.")
    var email: String,
    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    var password: String
)
