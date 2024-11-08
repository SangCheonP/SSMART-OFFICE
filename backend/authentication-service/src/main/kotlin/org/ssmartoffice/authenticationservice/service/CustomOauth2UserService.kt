package org.ssmartoffice.authenticationservice.service

import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.ssmartoffice.authenticationservice.client.UserServiceClient
import org.ssmartoffice.authenticationservice.client.response.UserLoginResponse
import org.ssmartoffice.authenticationservice.domain.CustomUserDetails
import org.ssmartoffice.authenticationservice.domain.OAuth2UserInfo
import org.ssmartoffice.authenticationservice.domain.Role
import org.ssmartoffice.authenticationservice.exception.AuthErrorCode

@Service
class CustomOauth2UserService(
    val userServiceClient: UserServiceClient
) : DefaultOAuth2UserService() {

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val provider = userRequest.clientRegistration.registrationId
        val oauth2UserInfo = OAuth2UserInfo.of(provider, oAuth2User.attributes)

        //구글 로그인 정보 기반으로 user-service 에서 정보 받아오기
        val commonResponse = userServiceClient.getIdAndRole(oauth2UserInfo.email)
        val dataMap = commonResponse.body?.data as? Map<*, *>
        val userLoginResponse = dataMap?.let {
            UserLoginResponse(
                userId = (it["userId"] as? Number)?.toLong(),
                role = it["role"] as? String ?: Role.USER.toString()
            )
        } ?: throw AuthenticationServiceException(AuthErrorCode.USER_RESPONSE_EXCEPTION.toString())

        return CustomUserDetails(
            userId = userLoginResponse.userId ?: throw AuthenticationServiceException("User ID is missing"),
            role = userLoginResponse.role,
            email = oauth2UserInfo.email,
            password = ""
        )
    }
}
