package org.ssmartoffice.authenticationservice.global.auth

import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.ssmartoffice.authenticationservice.user.client.UserServiceClient
import org.ssmartoffice.authenticationservice.user.client.response.UserLoginResponse

@Service
class CustomUserDetailsService(
    private val userServiceClient: UserServiceClient
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val commonResponse = userServiceClient.getIdAndRole(email)
        val userLoginResponse = commonResponse.body?.data as? UserLoginResponse
            ?: throw AuthenticationServiceException("Invalid login response structure")

        return CustomUserDetails(
            userId = userLoginResponse.userId,
            role = userLoginResponse.role,
            email = email,
            password = ""
        )
    }
}
