package org.ssmartoffice.authenticationservice.user.infrastructure

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.security.crypto.password.PasswordEncoder
import org.ssmartoffice.authenticationservice.user.domain.User

@Entity(name = "user")
class UserEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long? = null,
    val email: String = "",
    var password: String? = null,
    var role: String = "",
    refreshToken: String? = null
) {

    var refreshToken = refreshToken
        private set

    companion object {
        fun fromModel(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                password = user.password,
                role = user.role,
                email = user.email,
                refreshToken = user.refreshToken
            )
        }
    }


    fun encodePassword(encoder: PasswordEncoder) {
        this.password = encoder.encode(password)
    }

    fun toModel(): User {
        return User(
            id = id,
            password = password,
            role = role,
            email = email,
            refreshToken = refreshToken
        )
    }
}