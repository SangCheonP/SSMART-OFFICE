package org.ssmartoffice.seatservice.global.config

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Configuration
class FeignConfig {
    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor { template: RequestTemplate ->
            val requestAttributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
            val token = requestAttributes?.request?.getHeader("Authorization")
            if (token != null) {
                template.header("Authorization", token)
            }
        }
    }
}