package org.ssmartoffice.userservice.user.controller.request

import jakarta.validation.constraints.NotBlank

data class PasswordUpdateRequest(
    @field:NotBlank(message = "기존 비밀번호를 입력해주세요.")
    val oldPassword: String,
    @field:NotBlank(message = "새 비밀번호를 입력해주세요.")
    val newPassword: String,
)
