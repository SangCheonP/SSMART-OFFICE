package org.ssmartoffice.chatservice.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import org.ssmartoffice.chatservice.global.handler.ChatHandler

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val chatHandler: ChatHandler
) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/api/v1/chats/ws/queue", "/api/v1/chats/ws/topic")
        registry.setApplicationDestinationPrefixes("/api/v1/chats/ws/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/api/v1/chats/ws")
            .setAllowedOriginPatterns("*")
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(chatHandler)
    }
}
