package org.ssmartoffice.authenticationservice.security.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import feign.FeignException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.ssmartoffice.authenticationservice.global.jwt.JwtTokenProvider
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.ssmartoffice.authenticationservice.client.UserServiceClient
import org.ssmartoffice.authenticationservice.client.request.UserLoginRequest
import org.ssmartoffice.authenticationservice.client.response.UserLoginResponse
import org.ssmartoffice.authenticationservice.domain.CustomUserDetails
import org.ssmartoffice.authenticationservice.domain.Role
import org.ssmartoffice.authenticationservice.exception.UserErrorCode
import org.ssmartoffice.authenticationservice.exception.UserException
import org.ssmartoffice.authenticationservice.global.const.successcode.SuccessCode
import org.ssmartoffice.authenticationservice.global.dto.CommonResponse
import org.ssmartoffice.authenticationservice.security.handler.CustomAuthenticationFailureHandler
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
        jwtTokenProvider.createRefreshToken(authentication, response)
        sendLoginResponse(response, accessToken)
    }

    @Throws(IOException::class)
    private fun sendLoginResponse(response: HttpServletResponse, accessToken: String) {
        response.characterEncoding = "UTF-8"
        response.contentType = "application/json;charset=UTF-8"
        response.addHeader("Authorization", "Bearer $accessToken")

        val responseBody = CommonResponse(
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
            val commonResponse = userServiceClient.selfLogin(loginRequest)
                ?: throw AuthenticationServiceException(UserErrorCode.USER_RESPONSE_EXCEPTION.toString())
            val dataMap = commonResponse.body?.data as? Map<*, *>
            val userLoginResponse = dataMap?.let {
                UserLoginResponse(
                    userId = (it["userId"] as? Number)?.toLong(),
                    role = it["role"] as? String ?: Role.USER.toString()
                )
            } ?: throw AuthenticationServiceException(UserErrorCode.USER_RESPONSE_EXCEPTION.toString())


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
                else -> UserException(UserErrorCode.SERVER_COMMUNICATION_EXCEPTION)
            }
            throw AuthenticationServiceException(userException.message, userException)
        }

    }

    companion object {
        private val antPathMatcher = AntPathRequestMatcher("/api/v1/auth/login", "POST")
    }
}
