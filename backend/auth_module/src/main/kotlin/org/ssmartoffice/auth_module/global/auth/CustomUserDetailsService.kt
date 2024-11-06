package org.ssmartoffice.auth_module.global.auth

import org.ssmartoffice.auth_module.user.domain.User
import org.ssmartoffice.auth_module.user.service.port.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    val userRepository: UserRepository
) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(userId: String): UserDetails {
        val user: User? = userRepository.findByLoginId(userId)
        return CustomUserDetails(user!!)
    }
}
