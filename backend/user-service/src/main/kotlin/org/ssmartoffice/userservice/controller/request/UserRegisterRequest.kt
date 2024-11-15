package org.ssmartoffice.userservice.controller.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UserRegisterRequest(
    @field:Email(message = "이메일 형식이 올바르지 않습니다.", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @field:NotBlank(message = "이메일을 입력해주세요.")
    val email: String,

    @field:NotBlank(message = "비밀번호를 입력해주세요.")
    val password: String,

    @field:NotBlank(message = "이름을 입력해주세요.")
    val name: String,

    @field:NotBlank(message = "직급을 입력해주세요.")
    val position: String,

    @field:NotBlank(message = "직무를 입력해주세요.")
    val duty: String,

    @field:Pattern(message = "유효한 URL 형식이 아닙니다.", regexp = "(http|https)://[a-zA-Z0-9./\\-_~:%]+")
    @field:NotBlank(message = "이미지를 입력해주세요.")
    val profileImageUrl: String,

    @field:NotBlank(message = "사원 번호를 입력해주세요.")
    val employeeNumber: String,

    @field:Pattern(message = "유효한 전화번호 형식이 아닙니다.", regexp = "^\\d{3}-\\d{4}-\\d{4}$")
    val phoneNumber: String? = null,
)
