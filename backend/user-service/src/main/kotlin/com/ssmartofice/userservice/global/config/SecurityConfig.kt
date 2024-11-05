package com.ssmartofice.userservice.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain


@Configuration
class SecurityConfig{

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val authenticationManagerBuilder: AuthenticationManagerBuilder =
            http.getSharedObject(
                AuthenticationManagerBuilder::class.java
            )
        val authenticationManager: AuthenticationManager = authenticationManagerBuilder.build()

        http.authenticationManager(authenticationManager)

        http
            .httpBasic { obj: HttpBasicConfigurer<HttpSecurity?> -> obj.disable() }
            .cors(Customizer.withDefaults<CorsConfigurer<HttpSecurity>>())
            .csrf { obj: CsrfConfigurer<HttpSecurity?> -> obj.disable() }
            .formLogin { obj: FormLoginConfigurer<HttpSecurity?> -> obj.disable() }
            .rememberMe { obj: RememberMeConfigurer<HttpSecurity?> -> obj.disable() }
            .sessionManagement { management: SessionManagementConfigurer<HttpSecurity?> ->
                management.sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS
                )
            }

        http
            .authorizeHttpRequests { authz: AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry ->
                authz
//                    .requestMatchers("/users").hasRole("USER")
                    .requestMatchers("/admin").hasRole("ADMIN")
                    .anyRequest().permitAll()
            }

        return http.build()
    }


    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
