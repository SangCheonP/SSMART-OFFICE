package org.ssmartoffice.authenticationservice.global.security.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.ssmartoffice.authenticationservice.global.const.errorcode.AuthErrorCode
import org.ssmartoffice.authenticationservice.global.exception.AuthException
import org.ssmartoffice.authenticationservice.global.dto.ErrorResponse

class CustomAuthenticationFailureHandler : AuthenticationFailureHandler {
    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json;charset=UTF-8"

        val errorResponse = when {
            exception.cause is AuthException -> {
                val authException = exception.cause as AuthException
                when (authException.errorCode) {
                    AuthErrorCode.USER_NOT_FOUND -> {
                        response.status = HttpStatus.NOT_FOUND.value()
                        ErrorResponse(
                            status = HttpStatus.NOT_FOUND.value(),
                            error = "USER_NOT_FOUND",
                            message = "사용자를 찾을 수 없습니다."
                        )
                    }
                    AuthErrorCode.INVALID_OLD_PASSWORD -> {
                        response.status = HttpStatus.CONFLICT.value()
                        ErrorResponse(
                            status = HttpStatus.CONFLICT.value(),
                            error = "INVALID_OLD_PASSWORD",
                            message = "비밀번호가 일치하지 않습니다."
                        )
                    }
                    else -> {
                        response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
                        ErrorResponse(
                            status = HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            error = "INTERNAL_SERVER_ERROR",
                            message = "서버 오류가 발생했습니다."
                        )
                    }
                }
            }
            else -> {
                response.status = HttpStatus.UNAUTHORIZED.value()
                ErrorResponse(
                    status = HttpStatus.UNAUTHORIZED.value(),
                    error = "AUTHENTICATION_FAILED",
                    message = exception.message ?: "인증에 실패했습니다."
                )
            }
        }

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}