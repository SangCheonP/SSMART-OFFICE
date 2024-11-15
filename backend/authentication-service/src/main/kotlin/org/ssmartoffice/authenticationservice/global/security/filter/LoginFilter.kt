package org.ssmartoffice.authenticationservice.global.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import feign.FeignException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.ssmartoffice.authenticationservice.global.security.jwt.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.ssmartoffice.authenticationservice.client.UserServiceClient
import org.ssmartoffice.authenticationservice.client.request.UserLoginRequest
import org.ssmartoffice.authenticationservice.client.response.UserAuthenticationResponse
import org.ssmartoffice.authenticationservice.domain.CustomUserDetails
import org.ssmartoffice.authenticationservice.global.const.errorcode.AuthErrorCode
import org.ssmartoffice.authenticationservice.global.exception.AuthException
import org.ssmartoffice.authenticationservice.global.const.successcode.SuccessCode
import org.ssmartoffice.authenticationservice.global.dto.CommonResponse
import org.ssmartoffice.authenticationservice.global.dto.ErrorResponse
import org.ssmartoffice.authenticationservice.global.security.handler.CustomAuthenticationFailureHandler
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
        authentication: Authentication
    ) {
        val accessToken = jwtTokenProvider.createAccessToken(authentication)
        val refreshToken = jwtTokenProvider.createRefreshToken(authentication, response)
        if (refreshToken == null) {
            setErrorResponse(response, AuthErrorCode.CONNECTION_FAIL)
            return
        }
        sendLoginResponse(response, accessToken)
    }

    @Throws(IOException::class)
    private fun sendLoginResponse(response: HttpServletResponse, accessToken: String) {
        response.characterEncoding = "UTF-8"
        response.contentType = "application/json;charset=UTF-8"
        response.addHeader("Access-Control-Expose-Headers", "Authorization")
        response.addHeader("Authorization", accessToken)

        val responseBody = CommonResponse<Any>(
            status = SuccessCode.CREATED.getValue(),
            msg = "로그인에 성공했습니다."
        )
        response.writer.write(objectMapper.writeValueAsString(responseBody))
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val loginRequest = objectMapper.readValue(request.inputStream, UserLoginRequest::class.java)

        try {
            //user-service 에서 자체 로그인 후 정보 받아오기
            val userAuthenticationResponse: UserAuthenticationResponse = userServiceClient.selfLogin(loginRequest)?.body?.data
                ?: throw AuthenticationServiceException(AuthErrorCode.USER_RESPONSE_EXCEPTION.toString())

            val customUserDetails = CustomUserDetails(
                userId = userAuthenticationResponse.userId,
                role = userAuthenticationResponse.role,
                email = loginRequest.email,
                password = loginRequest.password
            )

            return UsernamePasswordAuthenticationToken(
                customUserDetails,
                customUserDetails.password,
                customUserDetails.authorities
            )

        } catch (e: FeignException) {
            val authException = when (e) {
                is FeignException.NotFound -> AuthException(AuthErrorCode.USER_NOT_FOUND)
                is FeignException.Conflict -> AuthException(AuthErrorCode.INVALID_OLD_PASSWORD)
                else -> AuthException(AuthErrorCode.SERVER_COMMUNICATION_EXCEPTION)
            }
            throw AuthenticationServiceException(authException.message, authException)
        }

    }

    companion object {
        private val antPathMatcher = AntPathRequestMatcher("/api/v1/auth/login", "POST")
    }

    private fun setErrorResponse(response: HttpServletResponse, errorCode: AuthErrorCode) {
        response.characterEncoding = "UTF-8"
        response.status = errorCode.httpStatus.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE

        val errorResponse = ErrorResponse(
            status = errorCode.httpStatus.value(),
            error = errorCode.name,
            message = errorCode.message
        )
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
        response.writer.flush()
    }

}
