package com.study.apigateway.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationEntryPoint : ServerAuthenticationEntryPoint {
    override fun commence(exchange: ServerWebExchange, ex: AuthenticationException): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.APPLICATION_JSON

        val errorResponse = mapOf(
            "status" to HttpStatus.UNAUTHORIZED.value(),
            "error" to "UNAUTHORIZED",
            "message" to "토큰이 유효하지 않습니다."
        )

        val buffer: DataBuffer = response.bufferFactory()
            .wrap(ObjectMapper().writeValueAsBytes(errorResponse))

        return response.writeWith(Mono.just(buffer))
    }
}