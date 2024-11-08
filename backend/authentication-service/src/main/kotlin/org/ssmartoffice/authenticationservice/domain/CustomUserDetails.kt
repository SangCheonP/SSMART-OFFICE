package org.ssmartoffice.authenticationservice.domain

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomUserDetails(
    val userId: Long?,
    private val role: String,
    val email: String,
    private val password: String,
    private var attributes: Map<String, Any>? = null,
    //리프레시 토큰 추가?
    refreshToken: String? = null
) : UserDetails, OAuth2User {

    private var refreshToken = refreshToken

    fun updateRefreshToken(refreshToken: String) {
        this.refreshToken = refreshToken
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_$role"))
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getAttributes(): Map<String, Any> {
        return attributes ?: emptyMap()
    }

    override fun getName(): String {
        return email
    }
}
