package com.smartoffice.apigateway.jwt

import io.jsonwebtoken.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import javax.crypto.SecretKey
import org.springframework.beans.factory.annotation.Value;


@Component
class JwtTokenProvider(
    @Value("\${app.auth.token.secret-key}")
    private val secretKey: String
) {
    private val key: SecretKey = Jwts.SIG.HS256.key().build()

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: JwtException) {
            false
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims: Claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

        val authorities: Collection<GrantedAuthority> =
            claims[AUTHORITIES_KEY]?.toString()?.split(",")
                ?.map { SimpleGrantedAuthority(it.trim()) }
                ?: emptyList()

        return UsernamePasswordAuthenticationToken(claims.subject, "", authorities)
    }

    companion object {
        private const val AUTHORITIES_KEY = "role"
    }
}