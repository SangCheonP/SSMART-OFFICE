package org.ssmartoffice.authenticationservice.service

import org.springframework.security.authentication.AuthenticationServiceException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.ssmartoffice.authenticationservice.client.UserServiceClient
import org.ssmartoffice.authenticationservice.domain.CustomUserDetails


@Service
class CustomUserDetailsService(
    private val userServiceClient: UserServiceClient
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails {
        val commonResponse = userServiceClient.getIdAndRole(email)
        val userLoginResponse = commonResponse.body?.data
            ?: throw AuthenticationServiceException("Invalid login response structure")

        return CustomUserDetails(
            userId = userLoginResponse.userId,
            role = userLoginResponse.role,
            email = email,
            password = ""
        )
    }
}
