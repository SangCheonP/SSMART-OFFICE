package org.example.auth_module.global.auth.domain

import org.example.auth_module.user.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User

class CustomUserDetails : UserDetails, OAuth2User {
    var user: User
        private set
    private var authorities: Collection<GrantedAuthority>? = null
    private var attributes: Map<String, Any>? = null

    constructor(user: User) {
        this.user = user
    }

    constructor(user: User, attributes: Map<String, Any>?) {
        this.user = user
        this.attributes = attributes
    }

    constructor(user: User, authorities: Collection<GrantedAuthority>?) {
        this.user = user
        this.authorities = authorities
    }

    val email: String
        get() = user.email

    override fun getName(): String {
        return user.name
    }

    override fun getAttributes(): Map<String, Any> {
        return attributes ?: emptyMap()
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        val authorities: MutableList<GrantedAuthority> = ArrayList()
        authorities.add(SimpleGrantedAuthority("ROLE_" + user.role))
        return authorities
    }

    override fun getPassword(): String {
        return user.password ?: ""
    }

    override fun getUsername(): String {
        return user.name
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
}
