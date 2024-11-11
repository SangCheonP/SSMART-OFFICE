package org.ssmartoffice.authenticationservice.infrastructure

import com.nimbusds.oauth2.sdk.util.StringUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.ssmartoffice.authenticationservice.global.util.CookieUtil
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.stereotype.Component

@Component
class CookieAuthorizationRequestRepository : AuthorizationRequestRepository<OAuth2AuthorizationRequest?> {

    val cookieUtil = CookieUtil()

    override fun loadAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        return cookieUtil.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
            .map { cookie -> cookieUtil.deserialize(cookie, OAuth2AuthorizationRequest::class.java) }
            .orElse(null)
    }

    override fun saveAuthorizationRequest(
        authorizationRequest: OAuth2AuthorizationRequest?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        if (authorizationRequest == null) {
            cookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
            cookieUtil.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME)
            return
        }

        cookieUtil.addCookie(
            response,
            OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
            cookieUtil.serialize(authorizationRequest),
            COOKIE_EXPIRE_SECONDS
        )
        val redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME)

        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            cookieUtil.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, COOKIE_EXPIRE_SECONDS)
        }
    }

    override fun removeAuthorizationRequest(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): OAuth2AuthorizationRequest? {
        return this.loadAuthorizationRequest(request)
    }

    fun removeAuthorizationRequestCookies(request: HttpServletRequest, response: HttpServletResponse) {
        cookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
        cookieUtil.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME)
    }

    companion object {
        const val OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME: String = "oauth_auth_request"
        const val REDIRECT_URI_PARAM_COOKIE_NAME: String = "redirect_uri"
        private const val COOKIE_EXPIRE_SECONDS = 180
    }
}
