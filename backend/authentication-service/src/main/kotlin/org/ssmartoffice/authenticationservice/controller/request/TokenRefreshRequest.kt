package org.ssmartoffice.authenticationservice.controller.request

import jakarta.validation.constraints.NotBlank

data class TokenRefreshRequest(
    @field:NotBlank(message = "만료된 엑세스 토큰을 담아주세요.")
    val expiredAccessToken: String,
    @field:NotBlank(message = "리프레시 토큰을 담아주세요.")
    val refreshToken: String,
)
