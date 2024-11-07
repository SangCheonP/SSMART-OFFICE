package org.ssmartoffice.authenticationservice.auth.infrastructure.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import org.ssmartoffice.authenticationservice.auth.exception.UserErrorCode
import java.io.IOException

@Component
class JwtAccessDeniedHandler : AccessDeniedHandler {
    @Throws(IOException::class)
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.contentType = "application/json;charset=UTF-8"
        response.status = HttpServletResponse.SC_UNAUTHORIZED

        val responseJson = ((("{" +
                "\"status\": \"" + UserErrorCode.ACCESS_DENIED.httpStatus.value()) + "\", " +
                "\"error\": \"" + UserErrorCode.ACCESS_DENIED.name) + "\", " +
                "\"msg\": \"" + UserErrorCode.ACCESS_DENIED.message) + "\"" +
                "}"

        response.writer.println(responseJson)
    }
}



