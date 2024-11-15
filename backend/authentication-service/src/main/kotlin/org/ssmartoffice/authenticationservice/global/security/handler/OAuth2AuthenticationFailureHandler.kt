package org.ssmartoffice.authenticationservice.global.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.ssmartoffice.authenticationservice.global.util.CookieUtil
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import org.ssmartoffice.authenticationservice.infrastructure.CookieAuthorizationRequestRepository
import java.io.IOException
import org.springframework.beans.factory.annotation.Value
import org.ssmartoffice.authenticationservice.global.const.errorcode.AuthErrorCode
import org.ssmartoffice.authenticationservice.global.exception.AuthException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Component
class OAuth2AuthenticationFailureHandler(
    val authorizationRequestRepository: CookieAuthorizationRequestRepository,
    @Value("\${app.oauth2.failureRedirectUri}") val failureRedirectUri: String
) : SimpleUrlAuthenticationFailureHandler() {

    val cookieUtil = CookieUtil()

    @Throws(IOException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        var redirectUrl = UriComponentsBuilder.fromUriString(failureRedirectUri)

        if (exception is AuthException) {
            val encodedMessage = URLEncoder.encode(exception.message, StandardCharsets.UTF_8.toString())
            redirectUrl = redirectUrl.queryParam("code", exception.errorCode.name)
                .queryParam("message", encodedMessage)
        } else {
            val encodedMessage =
                URLEncoder.encode(AuthErrorCode.AUTHENTICATION_FAIL.message, StandardCharsets.UTF_8.toString())
            redirectUrl = redirectUrl.queryParam("code", AuthErrorCode.AUTHENTICATION_FAIL.name)
                .queryParam("message", encodedMessage)
        }
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response)
        redirectStrategy.sendRedirect(request, response, redirectUrl.build().toString())
    }
}