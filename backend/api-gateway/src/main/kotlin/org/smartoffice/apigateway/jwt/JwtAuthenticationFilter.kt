package org.smartoffice.apigateway.jwt

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.JwtException
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

private val logger = KotlinLogging.logger {}

@Component
class JwtAuthenticationFilter(
    private val tokenProvider: JwtTokenProvider
) : WebFilter {

    companion object {
        private val PUBLIC_PATHS = listOf(
            // 인증 관련
            "/api/v1/auth/login",
            "/api/v1/auth/oauth2/authorization/**",
            "/api/v1/auth/oauth2/code/**",
            "/api/v1/auth/token/refresh",

            // 내부 통신
            "/api/v1/users/internal/**",

            //하드웨어 통신
            "/api/v1/nfc-tokens/**",
            "/api/v1/chats/ws/**",
        )
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.uri.path
        if (isPublicPath(path)) {
            return chain.filter(exchange)
        }

        val token = extractToken(exchange.request)
        return tokenProvider.validateToken(token, exchange)
            .then(chain.filter(exchange))
    }

    private fun isPublicPath(path: String): Boolean {
        return PUBLIC_PATHS.any { pattern ->
            if (pattern.endsWith("/**")) {
                path.startsWith(pattern.removeSuffix("/**"))
            } else {
                path == pattern
            }
        }
    }

    private fun extractToken(request: ServerHttpRequest): String? {
        return request.headers.getFirst(HttpHeaders.AUTHORIZATION)?.let {
            if (it.startsWith("Bearer ", ignoreCase = true)) {
                it.substring(7)
            } else null
        }
    }
}