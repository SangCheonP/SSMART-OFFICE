package org.example.auth_module.global.auth.service

import org.example.auth_module.global.auth.domain.CustomUserDetails
import org.example.auth_module.global.auth.domain.OAuth2UserInfo
import org.example.auth_module.user.domain.User
import org.example.auth_module.user.service.port.UserRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service

@Service
class CustomOauth2UserService(
    val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)

        val provider = userRequest.clientRegistration.registrationId

        val oauth2UserInfo = OAuth2UserInfo.of(provider, oAuth2User.attributes)

        var user : User = userRepository.findByEmail(oauth2UserInfo.email) ?: oauth2UserInfo.toEntity()

        user = userRepository.save(user)

        return CustomUserDetails(user, oAuth2User.attributes)
    }
}
