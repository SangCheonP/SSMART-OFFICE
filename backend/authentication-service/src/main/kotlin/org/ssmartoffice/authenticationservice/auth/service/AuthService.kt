package org.ssmartoffice.authenticationservice.auth.service

import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.ssmartoffice.authenticationservice.global.jwt.JwtTokenProvider
import org.springframework.stereotype.Service

@Slf4j
@Service
@RequiredArgsConstructor
class AuthService(
    val tokenProvider: JwtTokenProvider
) {
    fun refreshToken(oldAccessToken: String): String {
        val authentication = tokenProvider.getAuthentication(oldAccessToken)

        return tokenProvider.createAccessToken(authentication)
    }
}
