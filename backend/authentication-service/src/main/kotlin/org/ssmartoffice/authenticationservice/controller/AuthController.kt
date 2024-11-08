package org.ssmartoffice.authenticationservice.controller

import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ssmartoffice.authenticationservice.controller.request.TokenRefreshRequest
import org.ssmartoffice.authenticationservice.domain.CustomUserDetails
import org.ssmartoffice.authenticationservice.global.dto.CommonResponse
import org.ssmartoffice.authenticationservice.service.AuthService

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
class AuthController(
    val authService: AuthService,
    val httpServletResponse: HttpServletResponse
) {

    @PostMapping("/token/refresh")
    fun refreshToken(@RequestBody request: TokenRefreshRequest): ResponseEntity<CommonResponse> {
        val newAccessToken = authService.refreshToken(request)
        httpServletResponse.addHeader("Authorization", "Bearer $newAccessToken")
        return CommonResponse.created("토큰 갱신에 성공했습니다.")
    }

    @PostMapping("/logout")
    fun refreshToken(authentication: Authentication): ResponseEntity<CommonResponse> {
        val userDetails = authentication.principal as CustomUserDetails
        println("userDetails = ${userDetails}")
        authService.deleteToken(userDetails)
        return CommonResponse.created("로그아웃에 성공했습니다.")
    }
}
