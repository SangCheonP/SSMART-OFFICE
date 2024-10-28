package org.example.auth_module.user.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import lombok.AccessLevel
import lombok.NoArgsConstructor
import org.example.auth_module.global.auth.Role
import org.springframework.security.crypto.password.PasswordEncoder

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
class User(
    val loginId: String?,
    var password: String?,
    var role: Role,
    val name: String,
    val email: String
) {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Int? = null
    fun encodePassword(encoder: PasswordEncoder) {
        this.password = encoder.encode(password)
    }
}