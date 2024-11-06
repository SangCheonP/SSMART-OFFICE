package org.example.auth_module.global.auth.jwt

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.auth_module.global.exception.errorcode.ErrorCode
import org.example.auth_module.global.exception.errorcode.UserErrorCode
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import java.net.http.HttpHeaders

@Component
class JwtAuthenticationEntryPoint : AuthenticationEntryPoint {
    @Throws(IOException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException?
    ) {
        val exception = request.getAttribute("exception") ?: ""
        val errorCode: ErrorCode

        /*
        토큰 없는 경우
        */
        if(exception == "") {
            setResponse(response, UserErrorCode.NO_TOKEN_EXCEPTION)
            return
        }

        /*
        토큰이 이상한 경우
        */
        if (exception == UserErrorCode.INVALID_TOKEN.name) {
            errorCode = UserErrorCode.INVALID_TOKEN
            setResponse(response, errorCode)
            return
        }

        /*
        토큰이 만료된 경우
        */
        if (exception == UserErrorCode.EXPIRED_TOKEN_EXCEPTION.name) {
            errorCode = UserErrorCode.EXPIRED_TOKEN_EXCEPTION
            setResponse(response, errorCode)
        }
    }


    /*
     한글 출력을 위해 getWriter 사용
    */
    @Throws(IOException::class)
    private fun setResponse(response: HttpServletResponse, errorCode: ErrorCode) {
        response.contentType = "application/json;charset=UTF-8"
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val responseJson = ((("{" +
                "\"status\": \"" + errorCode.httpStatus.value()) + "\", " +
                "\"error\": \"" + errorCode.name) + "\", " +
                "\"msg\": \"" + errorCode.message) + "\"" +
                "}"

        response.writer.println(responseJson)
    }
}
