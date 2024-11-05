package com.ssmartofice.userservice.global.config

import com.ssmartofice.userservice.global.jwt.JwtAuthenticationFilter
import com.ssmartofice.userservice.global.jwt.JwtUtil
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
class SecurityConfig(
    private val jwtUtil: JwtUtil
) {

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .httpBasic { it.disable() }
            .cors(Customizer.withDefaults())
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .rememberMe { it.disable() }
            .sessionManagement { session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtUtil),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/api/v1/users/me").authenticated()
                    .requestMatchers(HttpMethod.POST, "/api/v1/users").hasAuthority("ROLE_ADMIN")
                    .requestMatchers(HttpMethod.PATCH, "/api/v1/users/{userId}").hasAuthority("ROLE_ADMIN")
                    .requestMatchers("/api/v1/users/**").authenticated()
                    .anyRequest().permitAll()
            }

        return http.build()
    }

    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}