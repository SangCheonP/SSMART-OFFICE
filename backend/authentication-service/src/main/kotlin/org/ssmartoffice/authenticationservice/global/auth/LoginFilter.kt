package org.ssmartoffice.authenticationservice.global.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import feign.FeignException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.ssmartoffice.authenticationservice.global.auth.jwt.JwtTokenProvider
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.ssmartoffice.authenticationservice.user.client.UserServiceClient
import org.ssmartoffice.authenticationservice.user.client.request.UserLoginRequest
import org.ssmartoffice.authenticationservice.user.client.response.UserLoginResponse
import org.ssmartoffice.authenticationservice.user.exception.UserException
import org.ssmartoffice.authenticationservice.user.exception.UserErrorCode
import java.io.IOException

class LoginFilter(
    authenticationManager: AuthenticationManager?,
    private val jwtTokenProvider: JwtTokenProvider,
    private val userServiceClient: UserServiceClient
) :
    AbstractAuthenticationProcessingFilter(
        antPathMatcher, authenticationManager
    ) {
    private val objectMapper = ObjectMapper().registerModule(
        KotlinModule.Builder()
            .build()
    )

    init {
        setAuthenticationFailureHandler(CustomAuthenticationFailureHandler())
    }

    @Throws(IOException::class, ServletException::class)
    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        auth: Authentication
    ) {
        val accessToken = jwtTokenProvider.createAccessToken(auth)
        sendResponseHeaderAccessToken(response, accessToken)
    }

    @Throws(IOException::class)
    private fun sendResponseHeaderAccessToken(response: HttpServletResponse, accessToken: String) {
        response.contentType = "application/json"
        response.addHeader("Authorization", "Bearer $accessToken")

        val message: MutableMap<String, Any> = HashMap()
        message["msg"] = "login success"
        ResponseEntity.ok().body<Map<String, Any>>(message)
        val result = objectMapper.writeValueAsString(message)
        response.writer.write(result)
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val loginRequest = objectMapper.readValue(request.inputStream, UserLoginRequest::class.java)

        try {
            val commonResponse = userServiceClient.selfLogin(loginRequest)
                ?: throw AuthenticationServiceException("user-service 에서 로그인 실패")

            val userLoginResponse = commonResponse.body?.data as? UserLoginResponse
                ?: throw AuthenticationServiceException("Invalid login response structure")

            val customUserDetails = CustomUserDetails(
                userId = userLoginResponse.userId,
                role = userLoginResponse.role,
                email = loginRequest.email,
                password = loginRequest.password
            )

            return UsernamePasswordAuthenticationToken(
                customUserDetails,
                customUserDetails.password,
                customUserDetails.authorities
            )

        } catch (e: FeignException) {
            val userException = when (e) {
                is FeignException.NotFound -> UserException(UserErrorCode.USER_NOT_FOUND)
                is FeignException.Conflict -> UserException(UserErrorCode.INVALID_OLD_PASSWORD)
                else -> UserException(UserErrorCode.UNKNOWN_ERROR)
            }
            throw AuthenticationServiceException(userException.message, userException)
        }

    }

    companion object {
        private val antPathMatcher = AntPathRequestMatcher("/api/v1/auth/login", "POST")
    }
}
