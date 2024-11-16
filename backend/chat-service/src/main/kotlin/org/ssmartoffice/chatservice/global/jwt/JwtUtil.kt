package org.ssmartoffice.chatservice.global.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.security.Key

@Component
class JwtUtil(
    @Value("\${app.auth.token.secret-key}")
    val secretKey: String,
) {

    private val SECRET_KEY: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    fun getUserIdByToken(accessToken: String): Long {
        val token: String = accessToken.split(" ")[1]
        val claims: Claims = parseClaims(token)
        val id: Long = claims[ID_KEY, Long::class.java]
        return id
    }

    fun getUserEmailByToken(accessToken: String): String {
        val token: String = accessToken.split(" ")[1]
        val claims: Claims = parseClaims(token)
        val email: String = claims[EMAIL_KEY, String::class.java]
        return email
    }

    fun parseClaims(token: String): Claims {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token).body
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }

    fun getAuthentication(accessToken: String): Authentication {
        val claims: Claims = parseClaims(accessToken)

        val role = claims[AUTHORITIES_KEY, String::class.java]
        val id= claims[ID_KEY, Integer::class.java].toLong()
        val authorities = listOf(SimpleGrantedAuthority(role))
        val authentication = UsernamePasswordAuthenticationToken(id, null, authorities)

        return authentication
    }

    companion object {
        const val AUTHORITIES_KEY = "role"
        const val ID_KEY = "id"
        const val EMAIL_KEY = "email"
    }
}