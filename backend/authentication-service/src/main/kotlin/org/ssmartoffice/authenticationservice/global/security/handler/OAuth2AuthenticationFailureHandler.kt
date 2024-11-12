package org.ssmartoffice.authenticationservice.global.security.handler

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.ssmartoffice.authenticationservice.global.util.CookieUtil
import org.springframework.security.core.AuthenticationException
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import org.ssmartoffice.authenticationservice.infrastructure.CookieAuthorizationRequestRepository
import org.ssmartoffice.authenticationservice.infrastructure.CookieAuthorizationRequestRepository.Companion.REDIRECT_URI_PARAM_COOKIE_NAME
import java.io.IOException

@Component
class OAuth2AuthenticationFailureHandler(
    val authorizationRequestRepository: CookieAuthorizationRequestRepository
) : SimpleUrlAuthenticationFailureHandler() {

    val cookieUtil = CookieUtil()

    @Throws(IOException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        if (exception is OAuth2AuthenticationException) {
            println("⭐⭐⭐⭐⭐⭐")
            response.sendRedirect("http://localhost:3000/error")
            return
        }

        // 무조건 특정 에러 페이지로 리다이렉트
        val errorRedirectUrl = "https://k11b202.p.ssafy.io/accessToken=error"

        // 오류 메시지 추가
        val redirectUrl = UriComponentsBuilder.fromUriString(errorRedirectUrl)
            .queryParam("error", exception.localizedMessage)
            .build().toUriString()

        // 쿠키 정리
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response)

        // 에러 페이지로 리다이렉트
        redirectStrategy.sendRedirect(request, response, redirectUrl)

//        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response)
//        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}
