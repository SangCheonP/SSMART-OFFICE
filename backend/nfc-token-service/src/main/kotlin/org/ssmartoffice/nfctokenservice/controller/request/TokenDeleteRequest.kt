package org.ssmartoffice.nfctokenservice.controller.request

import jakarta.validation.constraints.NotBlank

data class TokenDeleteRequest(
    @field:NotBlank(message = "이메일을 입력해주세요.")
    val email: String,
    @field:NotBlank(message = "하드웨어 인증코드를 입력해주세요.")
    val authCode: String,
)
