package org.ssmartoffice.chatservice.global.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.AntPathMatcher
import org.springframework.web.filter.OncePerRequestFilter


class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        if (isUriMatched("/api/v1/chats/ws/**", request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        val authHeader = request.getHeader("Authorization")
        val claims = jwtUtil.parseClaims(authHeader.substring(7))
        val role = claims[JwtUtil.AUTHORITIES_KEY, String::class.java]

        val id= claims[JwtUtil.ID_KEY, Integer::class.java].toLong()
        val email = claims.subject

        val authorities = listOf(SimpleGrantedAuthority(role))
        val authentication = UsernamePasswordAuthenticationToken(id, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)
    }

    private fun isUriMatched(pattern: String?, uri: String?): Boolean {
        val matcher = AntPathMatcher()
        return matcher.match(pattern!!, uri!!)
    }
}