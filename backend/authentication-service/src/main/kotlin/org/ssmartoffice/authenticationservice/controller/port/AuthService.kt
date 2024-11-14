package org.ssmartoffice.authenticationservice.controller.port

import org.ssmartoffice.authenticationservice.controller.request.TokenRefreshRequest
import org.ssmartoffice.authenticationservice.domain.CustomUserDetails

interface AuthService {
    fun refreshToken(request: TokenRefreshRequest): String?
    fun deleteToken(userDetails: CustomUserDetails): Boolean
}