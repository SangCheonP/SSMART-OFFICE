package com.ssmartofice.userservice.global.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        val claims = jwtUtil.parseClaims(authHeader.substring(7))
        val role = claims[JwtUtil.AUTHORITIES_KEY, String::class.java]
        val email= claims[JwtUtil.EMAIL_KEY, String::class.java]

        log.info(claims)

        val authorities = listOf(SimpleGrantedAuthority(role))
        val authentication = UsernamePasswordAuthenticationToken(email, null, authorities)
        SecurityContextHolder.getContext().authentication = authentication
        filterChain.doFilter(request, response)
    }
}