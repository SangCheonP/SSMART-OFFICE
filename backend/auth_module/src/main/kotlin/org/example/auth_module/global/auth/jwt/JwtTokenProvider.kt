package org.example.auth_module.global.auth.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.example.auth_module.global.auth.domain.CustomUserDetails
import org.example.auth_module.global.exception.errorcode.UserErrorCode
import org.example.auth_module.user.domain.User
import org.example.auth_module.user.service.port.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import java.util.stream.Collectors


@Component
class JwtTokenProvider(
    val userRepository: UserRepository,
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

        val userId: String = user.name
        val email: String = user.email

        val role: String = authentication.authorities.stream()
            .map { obj: GrantedAuthority -> obj.getAuthority() }
            .collect(Collectors.joining(","))

        return Jwts.builder()
            .setSubject(userId)
            .claim(AUTHORITIES_KEY, role)
            .claim(EMAIL_KEY, email)
            .setIssuer("SSmartOffice")
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .compact()
    }

    private fun saveRefreshToken(authentication: Authentication, refreshToken: String) {
        val user : User = (authentication.principal as CustomUserDetails).user

        user.updateRefreshToken(refreshToken)

        userRepository.save(user)
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

        val authorities: Collection<GrantedAuthority> =
            Arrays.stream(claims.get(AUTHORITIES_KEY)
                .toString()
                .split(",".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray())
                .map { role: String? ->
                    SimpleGrantedAuthority(
                        role
                    )
                }.toList()


        val email: String = claims[EMAIL_KEY, String::class.java]

        val user: User = userRepository.findByEmail(email)!!

        val principal = CustomUserDetails(user, authorities)

        return UsernamePasswordAuthenticationToken(principal, "", authorities)
    }

    fun validateToken(token: String?, request: HttpServletRequest): Boolean {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(token).body
            return true
        } catch (e: ExpiredJwtException) {
            request.setAttribute("exception", UserErrorCode.EXPIRED_TOKEN_EXCEPTION.name)
        } catch (e: JwtException) {
            request.setAttribute("exception", UserErrorCode.INVALID_TOKEN.name)
        }
        return false
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
    }
}
