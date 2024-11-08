package org.ssmartoffice.authenticationservice.global.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import org.ssmartoffice.authenticationservice.domain.CustomUserDetails
import java.security.Key
import java.util.*


@Component
class JwtTokenProvider(
    @Value("\${app.auth.token.secret-key}")
    val secretKey: String,
    @Value("\${app.auth.token.refresh-token-key}")
    val COOKIE_REFRESH_TOKEN_KEY: String
) {

    final val SECRET_KEY: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    fun createAccessToken(authentication: Authentication): String {
        val now = Date()
        val validity = Date(now.time + ACCESS_TOKEN_EXPIRE_LENGTH)

        val user: CustomUserDetails = authentication.principal as CustomUserDetails

        return Jwts.builder()
            .setSubject(user.email)
            .claim(AUTHORITIES_KEY, user.authorities.first().authority)
            .claim(ID_KEY, user.userId)
            .setIssuer("SSmartOffice")
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact()
    }

    private fun saveRefreshToken(authentication: Authentication, refreshToken: String) {
//        val userId: Long? = (authentication.principal as CustomUserDetails).userId
        (authentication.principal as CustomUserDetails).updateRefreshToken(refreshToken)
        //TODO: 리프레시 토큰 저장
    }

    fun createRefreshToken(authentication: Authentication, response: HttpServletResponse) {
        val now = Date()
        val validity = Date(now.time + REFRESH_TOKEN_EXPIRE_LENGTH)

        val refreshToken = Jwts.builder()
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .setIssuer("bok")
            .setIssuedAt(now)
            .setExpiration(validity)
            .compact()

        saveRefreshToken(authentication, refreshToken)
        val cookie = ResponseCookie.from(COOKIE_REFRESH_TOKEN_KEY, refreshToken)
            .httpOnly(false)
            .secure(true)
            .sameSite("None")
            .maxAge(REFRESH_TOKEN_EXPIRE_LENGTH)
            .path("/")
            .build()

        response.addHeader("Set-Cookie", cookie.toString())
    }

    fun getAuthentication(accessToken: String): Authentication {
        val claims: Claims = parseClaims(accessToken)

        val role = claims[AUTHORITIES_KEY].toString()
        val email = claims.subject
        val userId = (claims[ID_KEY] as Number).toLong()

        val authority = SimpleGrantedAuthority("ROLE_$role")

        val customUserDetails = CustomUserDetails(
            userId = userId,
            role = role.replace("ROLE_", ""),  // ROLE_ prefix 없는 순수 role 값
            email = email,
            password = ""
        )

        println("😮😮😮😮customUserDetails = ${customUserDetails}")

        return UsernamePasswordAuthenticationToken(customUserDetails, "", listOf(authority))
    }

    fun validateToken(token: String?): Boolean {
        return try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token)
            true
        } catch (e: ExpiredJwtException) {
            false
        } catch (e: JwtException) {
            false
        }
    }

    private fun parseClaims(accessToken: String): Claims {
        return try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(accessToken).body
        } catch (e: ExpiredJwtException) {
            e.getClaims()
        }
    }

    companion object {
        private const val REFRESH_TOKEN_EXPIRE_LENGTH = 1000L * 60 * 60 * 24 * 7
        private const val ACCESS_TOKEN_EXPIRE_LENGTH = 1000L * 60 * 60 * 2 // 2시간
        private const val AUTHORITIES_KEY = "role"
        private const val EMAIL_KEY = "email"
        private const val ID_KEY = "id"
    }
}
