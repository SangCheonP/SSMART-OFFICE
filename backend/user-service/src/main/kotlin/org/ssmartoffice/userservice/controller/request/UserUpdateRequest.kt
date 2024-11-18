package org.ssmartoffice.userservice.controller.request

import jakarta.validation.constraints.Pattern

data class UserUpdateRequest(
    val name: String? = null,
    val position: String? = null,
    val duty: String? = null,
    @field:Pattern(message = "유효한 URL 형식이 아닙니다.", regexp = "(http|https)://[a-zA-Z0-9./\\-_~:%]+")
    val profileImageUrl: String? = null,
    @field:Pattern(message = "유효한 전화번호 형식이 아닙니다.", regexp = "^\\d{3}-\\d{4}-\\d{4}$")
    val phoneNumber: String? = null,
)
