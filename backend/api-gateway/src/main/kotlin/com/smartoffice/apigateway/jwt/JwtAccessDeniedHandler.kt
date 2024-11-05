package com.smartoffice.apigateway.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtAccessDeniedHandler : ServerAccessDeniedHandler {
    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.FORBIDDEN
        response.headers.contentType = MediaType.APPLICATION_JSON

        val errorResponse = mapOf(
            "status" to HttpStatus.FORBIDDEN.value(),
            "error" to "FORBIDDEN",
            "message" to "접근 권한이 없습니다."
        )

        val buffer: DataBuffer = response.bufferFactory()
            .wrap(ObjectMapper().writeValueAsBytes(errorResponse))

        return response.writeWith(Mono.just(buffer))
    }
}