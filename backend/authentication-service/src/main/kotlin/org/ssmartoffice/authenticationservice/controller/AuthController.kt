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
import org.ssmartoffice.authenticationservice.controller.port.AuthService
import org.ssmartoffice.authenticationservice.controller.request.TokenRefreshRequest
import org.ssmartoffice.authenticationservice.domain.CustomUserDetails
import org.ssmartoffice.authenticationservice.global.dto.CommonResponse

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
class AuthController(
    val authService: AuthService,
    val httpServletResponse: HttpServletResponse
) {

    @PostMapping("/token/refresh")
    fun refreshToken(@RequestBody request: TokenRefreshRequest): ResponseEntity<CommonResponse<Any>> {
        val newAccessToken = authService.refreshToken(request)
        httpServletResponse.addHeader("Authorization", newAccessToken)
        return CommonResponse.created("토큰 갱신에 성공했습니다.")
    }

    @PostMapping("/logout")
    fun refreshToken(authentication: Authentication): ResponseEntity<CommonResponse<Any>> {
        val userDetails = authentication.principal as CustomUserDetails
        if(authService.deleteToken(userDetails)){
            return CommonResponse.created("로그아웃에 성공했습니다.")
        }
        return CommonResponse.created("이미 로그아웃이 완료되었습니다.")
    }
}
