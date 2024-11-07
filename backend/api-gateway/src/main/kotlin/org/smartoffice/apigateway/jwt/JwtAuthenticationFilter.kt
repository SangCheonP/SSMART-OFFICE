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

    private val excludedPaths = listOf( //TODO: 수정 필요
        "/api/v1/auth/login",
        "/oauth2/authorization/google",
        "/login/oauth2/authorization/google",
        "/api/v1/auth/token/refresh",
        "/api/v1/users/internal/login"
    )

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val path = exchange.request.uri.path
        if (excludedPaths.any { path.startsWith(it) }) {
            return chain.filter(exchange)
        }

        val token = extractToken(exchange.request)

        logger.info { "token: $token" }

        return tokenProvider.validateToken(token, exchange)
            .then(chain.filter(exchange))

    }

    private fun extractToken(request: ServerHttpRequest): String? {
        return request.headers.getFirst(HttpHeaders.AUTHORIZATION)?.let {
            if (it.startsWith("Bearer ", ignoreCase = true)) {
                it.substring(7)
            } else null
        }
    }
}