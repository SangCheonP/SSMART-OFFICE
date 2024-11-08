package org.example.auth_module.global.config

import org.example.auth_module.global.auth.service.CustomOauth2UserService
import org.example.auth_module.global.auth.service.CustomUserDetailsService
import org.example.auth_module.global.auth.filter.LoginFilter
import org.example.auth_module.global.auth.handler.OAuth2AuthenticationFailureHandler
import org.example.auth_module.global.auth.handler.OAuth2AuthenticationSuccessHandler
import org.example.auth_module.global.auth.handler.JwtAccessDeniedHandler
import org.example.auth_module.global.auth.jwt.JwtAuthenticationEntryPoint
import org.example.auth_module.global.auth.filter.JwtAuthenticationFilter
import org.example.auth_module.global.auth.jwt.JwtTokenProvider
import org.example.auth_module.global.auth.repository.CookieAuthorizationRequestRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer
import org.springframework.security.config.annotation.web.configurers.RememberMeConfigurer
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity
@Configuration
class SecurityConfig(
    val customOauth2UserService: CustomOauth2UserService,
    val cookieAuthorizationRequestRepository: CookieAuthorizationRequestRepository,
    val oAuth2AuthenticationSuccessHandler: OAuth2AuthenticationSuccessHandler,
    val oAuth2AuthenticationFailureHandler: OAuth2AuthenticationFailureHandler,
    val jwtTokenProvider: JwtTokenProvider,
    val jwtAuthenticationEntryPoint: JwtAuthenticationEntryPoint,
    val jwtAccessDeniedHandler: JwtAccessDeniedHandler,
    val customUserDetailsService: CustomUserDetailsService
) {


    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        val authenticationManagerBuilder: AuthenticationManagerBuilder =
            http.getSharedObject(
                AuthenticationManagerBuilder::class.java
            )
        val authenticationManager: AuthenticationManager = authenticationManagerBuilder.build()

        val loginFilter = LoginFilter(authenticationManager, jwtTokenProvider)

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

        //oauth2Login
        http.oauth2Login { login: OAuth2LoginConfigurer<HttpSecurity?> ->
            login.authorizationEndpoint { authorizationEndpoint: OAuth2LoginConfigurer<HttpSecurity?>.AuthorizationEndpointConfig ->
                authorizationEndpoint.authorizationRequestRepository(
                    cookieAuthorizationRequestRepository
                )
            }
                .userInfoEndpoint { userInfoEndpoint: OAuth2LoginConfigurer<HttpSecurity?>.UserInfoEndpointConfig ->
                    userInfoEndpoint.userService(
                        customOauth2UserService
                    )
                } // 회원 정보 처리
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
        }

        http.exceptionHandling { handling: ExceptionHandlingConfigurer<HttpSecurity?> ->
            handling.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
        }

        //jwt filter 설정
        http
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter::class.java
            )
            .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }


    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
