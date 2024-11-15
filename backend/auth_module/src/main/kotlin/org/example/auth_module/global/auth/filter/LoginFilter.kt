package org.example.auth_module.global.auth.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.auth_module.global.auth.jwt.JwtTokenProvider
import org.example.auth_module.user.controller.request.UserLoginRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import java.io.IOException

class LoginFilter(authenticationManager: AuthenticationManager?, private val jwtTokenProvider: JwtTokenProvider) :
    AbstractAuthenticationProcessingFilter(
        antPathMatcher, authenticationManager
    ) {
    private val objectMapper = ObjectMapper().registerModule(
        KotlinModule.Builder()
            .build()
    )

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
        // accessToken header 전송
        response.contentType = "application/json"
        response.addHeader("Authorization", "Bearer $accessToken") // header에 accesstoken 추가

        val message: MutableMap<String, Any> = HashMap()
        message["msg"] = "login success"
        ResponseEntity.ok().body<Map<String, Any>>(message)
        val result = objectMapper.writeValueAsString(message)
        response.writer.write(result)
    }

    @Throws(AuthenticationException::class)
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        try {
            val user = objectMapper.readValue(request.inputStream, UserLoginRequest::class.java)
            val authenticationToken = UsernamePasswordAuthenticationToken(user.loginId, user.password)
            return authenticationManager.authenticate(authenticationToken)
        } catch (e: IOException) {
            e.printStackTrace()
            return authenticationManager.authenticate(UsernamePasswordAuthenticationToken(null, null))
        }
    }

    companion object {
        private val antPathMatcher = AntPathRequestMatcher("/login", "POST")
    }
}
