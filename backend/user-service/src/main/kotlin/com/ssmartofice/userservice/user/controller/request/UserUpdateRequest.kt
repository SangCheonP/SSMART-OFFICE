package com.ssmartofice.userservice.user.controller.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern

data class UserUpdateRequest(
    @field:Email(message = "이메일 형식이 올바르지 않습니다.", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    val email: String? = null,
    val password: String? = null,
    val name: String? = null,
    val position: String? = null,
    val duty: String? = null,
    @field:Pattern(message = "유효한 URL 형식이 아닙니다.", regexp = "(http|https)://[a-zA-Z0-9./]+")
    val profileImageUrl: String? = null,
    @field:Pattern(message = "유효한 전화번호 형식이 아닙니다.", regexp = "^\\\\d{3}-\\\\d{4}-\\\\d{4}\$")
    val phoneNumber: String? = null,
)
