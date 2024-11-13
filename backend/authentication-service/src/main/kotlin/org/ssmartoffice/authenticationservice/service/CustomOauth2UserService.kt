package org.ssmartoffice.authenticationservice.service

import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.ssmartoffice.authenticationservice.client.UserServiceClient
import org.ssmartoffice.authenticationservice.domain.CustomUserDetails
import org.ssmartoffice.authenticationservice.domain.OAuth2UserInfo
import org.ssmartoffice.authenticationservice.global.const.errorcode.AuthErrorCode
import org.ssmartoffice.authenticationservice.global.exception.AuthException

@Service
class CustomOauth2UserService(
    val userServiceClient: UserServiceClient
) : DefaultOAuth2UserService() {
    private val logger = LoggerFactory.getLogger(CustomOauth2UserService::class.java)

    @Throws(OAuth2AuthenticationException::class)
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val provider = userRequest.clientRegistration.registrationId
        val oauth2UserInfo = OAuth2UserInfo.of(provider, oAuth2User.attributes)

        try {
            val commonResponse = userServiceClient.getIdAndRole(oauth2UserInfo.email)
            val userLoginResponse = commonResponse.body?.data ?: throw AuthException(AuthErrorCode.USER_NOT_FOUND)

            return CustomUserDetails(
                userId = userLoginResponse.userId,
                role = userLoginResponse.role,
                email = oauth2UserInfo.email,
                password = ""
            )
        } catch (e: FeignException.NotFound) {
            logger.error("이메일 찾을 수 없음: ${oauth2UserInfo.email}")
            throw AuthException(AuthErrorCode.USER_NOT_FOUND) //TODO: 사용자 없는 에러와 그냥 에러 구분해서 쿼리 파라미터 실어 보내기
        } catch (e: Exception) {
            logger.error(e.message)
            throw AuthException(AuthErrorCode.SERVER_COMMUNICATION_EXCEPTION)
        }
    }
}
