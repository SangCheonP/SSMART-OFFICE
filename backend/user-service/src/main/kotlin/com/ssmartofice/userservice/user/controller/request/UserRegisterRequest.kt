package com.ssmartofice.userservice.user.controller.request

import jakarta.validation.constraints.NotBlank

data class UserRegisterRequest(
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

    @field:NotBlank(message = "이미지를 입력해주세요.")
    val profileImageUrl: String
)
