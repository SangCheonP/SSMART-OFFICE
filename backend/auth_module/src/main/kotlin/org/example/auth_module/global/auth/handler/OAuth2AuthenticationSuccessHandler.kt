package org.example.auth_module.global.auth.handler

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.extern.slf4j.Slf4j
import org.example.auth_module.global.auth.jwt.JwtTokenProvider
import org.example.auth_module.global.auth.repository.CookieAuthorizationRequestRepository
import org.example.auth_module.global.auth.repository.CookieAuthorizationRequestRepository.Companion.REDIRECT_URI_PARAM_COOKIE_NAME
import org.example.auth_module.global.exception.RestApiException
import org.example.auth_module.global.exception.errorcode.CommonErrorCode
import org.example.auth_module.global.exception.errorcode.ErrorCode
import org.example.auth_module.global.exception.errorcode.UserErrorCode
import org.example.auth_module.global.util.CookieUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.io.IOException
import java.io.PrintWriter
import java.net.URI
import java.util.*

@Slf4j
@Component
class OAuth2AuthenticationSuccessHandler(
    val tokenProvider: JwtTokenProvider,
    val authorizationRequestRepository: CookieAuthorizationRequestRepository
) : SimpleUrlAuthenticationSuccessHandler() {

    @Value("\${app.oauth2.authorizedRedirectUri}")
    val redirectUri: String = ""
    val cookieUtil = CookieUtil()

    @Throws(IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val targetUrl = determineTargetUrl(request, response, authentication)

        if (response.isCommitted) {
            return
        }
        clearAuthenticationAttributes(request, response)
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }

    override fun determineTargetUrl(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ): String {
        val redirectUri: Optional<String> = cookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
            .map { obj: Cookie -> obj.value }

        if (redirectUri.isPresent && !isAuthorizedRedirectUri(redirectUri.get())) {
            val errorCode: ErrorCode = UserErrorCode.NOT_MATCHED_REDIRECT_URI
            try {
                setResponse(response, errorCode)
            } catch (e: IOException) {
                throw RestApiException(CommonErrorCode.INTERNAL_SERVER_ERROR)
            }
        }
        // String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
        val targetUrl = redirectUri.orElse("http://localhost:3000")
        val accessToken: String = tokenProvider.createAccessToken(authentication)
        tokenProvider.createRefreshToken(authentication, response)

        return UriComponentsBuilder.fromUriString(targetUrl)
            .queryParam("accessToken", accessToken)
            .build().toUriString()
    }

    protected fun clearAuthenticationAttributes(request: HttpServletRequest, response: HttpServletResponse) {
        super.clearAuthenticationAttributes(request)
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response)
    }

    private fun isAuthorizedRedirectUri(uri: String): Boolean {
        val clientRedirectUri = URI.create(uri)
        val authorizedUri = URI.create(redirectUri)

        return authorizedUri.host.equals(
            clientRedirectUri.host,
            ignoreCase = true
        ) && authorizedUri.port == clientRedirectUri.port
    }

    @Throws(IOException::class)
    private fun setResponse(response: HttpServletResponse, errorCode: ErrorCode) {
        response.contentType = "application/json;charset=UTF-8"
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val responseJson = StringBuilder()
        responseJson.append("{")
            .append("\"status\": \"").append(errorCode.httpStatus).append("\", ")
            .append("\"error\": \"").append(errorCode).append("\", ")
            .append("\"msg\": \"").append(errorCode.message).append("\"")
            .append("}")

        val writer: PrintWriter = response.writer
        writer.println(responseJson)
    }
}
