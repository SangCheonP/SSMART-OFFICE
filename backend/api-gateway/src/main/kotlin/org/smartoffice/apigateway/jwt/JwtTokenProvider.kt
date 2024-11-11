package org.smartoffice.apigateway.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import org.smartoffice.apigateway.exception.errorcode.JwtErrorCode
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.security.Key


private val logger = KotlinLogging.logger {}

@Component
class JwtTokenProvider(
    @Value("\${app.auth.token.secret-key}")
    private val secretKey: String,
) {
    private final val key: Key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey))

    fun validateToken(token: String?, exchange: ServerWebExchange): Mono<Void> {

        if(token == null) {
            return setResponse(exchange, JwtErrorCode.NO_TOKEN_EXCEPTION)
        }

        println("token = ${token}")
        try {
            val result = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).body
            logger.info { "result: $result" }
            return Mono.empty()
        } catch (e: ExpiredJwtException) {
            return setResponse(exchange, JwtErrorCode.EXPIRED_TOKEN_EXCEPTION)
        } catch (e: JwtException) {
            return setResponse(exchange, JwtErrorCode.INVALID_TOKEN)
        }
    }

    private fun setResponse(exchange: ServerWebExchange, errorCode: JwtErrorCode): Mono<Void> {
        val errorAttributes: MutableMap<String, Any> = HashMap()
        errorAttributes["status"] = errorCode.httpStatus.value()
        errorAttributes["name"] = errorCode.name
        errorAttributes["message"] = errorCode.message

        val response = exchange.response
        response.statusCode = errorCode.httpStatus
        response.headers.contentType = MediaType.APPLICATION_JSON

        val buffer = response.bufferFactory().wrap(ObjectMapper().writeValueAsBytes(errorAttributes))
        return response.writeWith(Mono.just(buffer))
    }

}