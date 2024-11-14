package org.ssmartoffice.authenticationservice.global.security.jwt

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import org.ssmartoffice.authenticationservice.domain.CustomUserDetails
import java.security.Key
import java.util.*
import java.time.Duration

private val logger = KotlinLogging.logger {}

@Component
class JwtTokenProvider(
    @Value("\${app.auth.token.secret-key}")
    val secretKey: String,
    @Value("\${app.auth.token.refresh-token-key}")
    val COOKIE_REFRESH_TOKEN_KEY: String,
) {
    @Autowired
    private lateinit var redisTemplate: RedisTemplate<String, String>

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
        val userDetails = authentication.principal as CustomUserDetails
        val expireDuration = Duration.ofMillis(REFRESH_TOKEN_EXPIRE_LENGTH)
        redisTemplate.opsForValue().set("${userDetails.email}-refresh", refreshToken, expireDuration)
    }

    fun createRefreshToken(authentication: Authentication, response: HttpServletResponse): String? {
        val now = Date()
        val validity = Date(now.time + REFRESH_TOKEN_EXPIRE_LENGTH)
        val userDetails = authentication.principal as CustomUserDetails

        val refreshToken = Jwts.builder()
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
            .setSubject(userDetails.email)
            .setIssuer("SSmartOffice")
            .setIssuedAt(now)
            .setExpiration(validity)
            .compact()

        return try {
            saveRefreshToken(authentication, refreshToken)

            // 쿠키 설정
            val cookie = ResponseCookie.from(COOKIE_REFRESH_TOKEN_KEY, refreshToken)
                .httpOnly(false)
                .secure(true)
                .sameSite("None")
                .maxAge(REFRESH_TOKEN_EXPIRE_LENGTH)
                .path("/")
                .build()

            response.addHeader("Set-Cookie", cookie.toString())
            refreshToken  // 성공 시 refreshToken 반환
        } catch (e: Exception) {
            logger.error(e) { "Failed to save refresh token" }
            null  // 예외 발생 시 null 반환
        }
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
        return UsernamePasswordAuthenticationToken(customUserDetails, "", listOf(authority))
    }

    private fun parseClaims(accessToken: String): Claims {
        return try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parseClaimsJws(accessToken).body
        } catch (e: ExpiredJwtException) {
            e.getClaims()
        }
    }

    fun checkIsExistRefreshToken(userDetails: CustomUserDetails): Boolean {
        return redisTemplate.hasKey("${userDetails.email}-refresh")
    }

    fun checkMatchRefreshToken(userDetails: CustomUserDetails, refreshToken: String): Boolean {
        val storedToken = redisTemplate.opsForValue().get("${userDetails.email}-refresh")
        return refreshToken == storedToken
    }

    fun deleteRefreshToken(userDetails: CustomUserDetails): Boolean {
        val key = "${userDetails.email}-refresh"
        if (redisTemplate.opsForValue().get("${userDetails.email}-refresh") == null) {
            return false
        }
        redisTemplate.delete(key)
        return true
    }

    companion object {
        //        private const val REFRESH_TOKEN_EXPIRE_LENGTH = 1000L * 60 * 60 * 24 * 7
//        private const val ACCESS_TOKEN_EXPIRE_LENGTH = 1000L * 60 * 60 * 2 // 2시간
        private const val REFRESH_TOKEN_EXPIRE_LENGTH = 1000L * 60 * 60 * 24 * 15
        private const val ACCESS_TOKEN_EXPIRE_LENGTH = 1000L * 60 * 60 * 24 * 15 // 15일
        private const val AUTHORITIES_KEY = "role"
        private const val ID_KEY = "id"
    }
}
