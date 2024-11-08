package org.ssmartoffice.authenticationservice.service

import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.ssmartoffice.authenticationservice.global.jwt.JwtTokenProvider
import org.springframework.stereotype.Service
import org.ssmartoffice.authenticationservice.controller.request.TokenRefreshRequest
import org.ssmartoffice.authenticationservice.domain.CustomUserDetails
import org.ssmartoffice.authenticationservice.exception.AuthErrorCode
import org.ssmartoffice.authenticationservice.exception.AuthException

@Slf4j
@Service
@RequiredArgsConstructor
class AuthService(
    val tokenProvider: JwtTokenProvider,
    val httpServletResponse: HttpServletResponse,
) {
    fun refreshToken(tokenRefreshRequest: TokenRefreshRequest): String {
        val authentication = tokenProvider.getAuthentication(tokenRefreshRequest.expiredAccessToken)
        val userDetails = authentication.principal as CustomUserDetails
        if (!tokenProvider.checkIsExistRefreshToken(userDetails)) {
            throw AuthException(AuthErrorCode.NOT_FOUND_REFRESH_TOKEN)
        }
        if (!tokenProvider.checkMatchRefreshToken(userDetails, tokenRefreshRequest.refreshToken)) {
            throw AuthException(AuthErrorCode.NOT_MATCH_REFRESH_TOKEN)
        }
        val accessToken = tokenProvider.createAccessToken(authentication)
        tokenProvider.createRefreshToken(authentication, httpServletResponse)
        return accessToken
    }

    fun deleteToken(userDetails: CustomUserDetails) {
        tokenProvider.deleteRefreshToken(userDetails)
    }

}
