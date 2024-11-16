package org.ssmartoffice.chatservice.global.handler

import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.core.Authentication
import org.ssmartoffice.chatservice.global.jwt.JwtUtil

@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
class ChatHandler(
    private val tokenProvider: JwtUtil
) : ChannelInterceptor {
    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor =
            MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        if (StompCommand.CONNECT == accessor!!.command) {
            val token = accessor.getNativeHeader("Authorization").toString()

            val auth: Authentication = tokenProvider.getAuthentication(token.substring(1, token.length - 1))

            accessor.user = auth
        }
        return message
    }
}
