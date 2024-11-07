package org.ssmartoffice.authenticationservice.global.auth.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtAuthenticationFilter(
    private val tokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = parseBearerToken(request)

        if (token != null && tokenProvider.validateToken(token)) {
            val authentication = tokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }
        filterChain.doFilter(request, response)
    }

    private fun parseBearerToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader("Authorization")
        return if (!bearerToken.isNullOrEmpty() && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }
}