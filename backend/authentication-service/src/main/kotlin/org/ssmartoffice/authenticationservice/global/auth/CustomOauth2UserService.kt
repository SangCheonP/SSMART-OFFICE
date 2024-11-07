package org.ssmartoffice.authenticationservice.global.auth

import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.ssmartoffice.authenticationservice.user.client.UserServiceClient
import org.ssmartoffice.authenticationservice.user.client.response.UserLoginResponse

@Service
class CustomOauth2UserService(
    val userServiceClient: UserServiceClient
) : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        println("⭐⭐⭐⭐⭐⭐userRequest = $userRequest")
        val oAuth2User = super.loadUser(userRequest)
        println("⭐oAuth2User = $oAuth2User")
        val provider = userRequest.clientRegistration.registrationId
        println("⭐provider = $provider")
        val oauth2UserInfo = OAuth2UserInfo.of(provider, oAuth2User.attributes)
        println("⭐oauth2UserInfo = $oauth2UserInfo")

        val commonResponse = userServiceClient.getIdAndRole(oauth2UserInfo.email)
        val dataMap = commonResponse.body?.data as? Map<*, *>
        val userLoginResponse = dataMap?.let {
            UserLoginResponse(
                userId = (it["userId"] as? Number)?.toLong(),
                role = it["role"] as? String ?: "UNKNOWN"
            )
        } ?: throw AuthenticationServiceException("Invalid login response structure")

        println("🔥🔥🔥🔥🔥🔥userLoginResponse = $userLoginResponse")
        return CustomUserDetails(
            userId = userLoginResponse.userId ?: throw AuthenticationServiceException("User ID is missing"),
            role = userLoginResponse.role,
            email = oauth2UserInfo.email,
            password = ""
        )
    }
}
