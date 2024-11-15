package org.example.auth_module.user.infrastructure

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.example.auth_module.global.auth.domain.Role
import org.example.auth_module.user.domain.User
import org.springframework.security.crypto.password.PasswordEncoder

@Entity(name="user")
class UserEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Int?,
    val loginId: String?,
    var password: String?,
    var role: Role,
    val name: String,
    val email: String,
    refreshToken: String?
) {

    var refreshToken = refreshToken
    private set

    companion object {
        fun fromModel(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                loginId = user.loginId,
                password = user.password,
                role = user.role,
                name = user.name,
                email = user.email,
                refreshToken = user.refreshToken)
        }
    }


    fun encodePassword(encoder: PasswordEncoder) {
        this.password = encoder.encode(password)
    }

    fun toModel(): User {
        return User(
            id = id,
            loginId = loginId,
            password = password,
            role = role,
            name = name,
            email = email,
            refreshToken = refreshToken)
    }
}