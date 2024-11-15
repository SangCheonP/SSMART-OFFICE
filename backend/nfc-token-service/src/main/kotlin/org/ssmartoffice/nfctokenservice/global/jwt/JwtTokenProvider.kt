package org.ssmartoffice.nfctokenservice.global.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.apache.tomcat.util.net.openssl.ciphers.Authentication
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import java.util.stream.Collectors


@Component
class JwtTokenProvider(
    @Value("\${app.auth.token.secret-key}")
    val secretKey: String,
) {


    final val SECRET_KEY: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    fun createToken(
        userId: String,
        email: String): String {
        val now = Date()
        val validity = Date(now.time + ACCESS_TOKEN_EXPIRE_LENGTH)


        return Jwts.builder()
            .setSubject(userId)
            .claim(EMAIL_KEY, email)
            .setIssuer("SSmartOffice")
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact()
    }


    private fun parseClaims(accessToken: String): Claims {
        return try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(accessToken).body
        } catch (e: ExpiredJwtException) {
            e.getClaims()
        }
    }

    companion object {
        private const val ACCESS_TOKEN_EXPIRE_LENGTH = 1000L * 60 * 60 * 200 // 2시간
        private const val AUTHORITIES_KEY = "role"
        private const val EMAIL_KEY = "email"
    }
}
