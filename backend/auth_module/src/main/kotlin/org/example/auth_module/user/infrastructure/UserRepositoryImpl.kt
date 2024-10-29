package org.example.auth_module.user.infrastructure

import org.example.auth_module.user.domain.User
import org.example.auth_module.user.service.port.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {

    override fun findByEmail(email: String?): User? {
        return userJpaRepository.findByEmail(email)?.toModel()
    }

    override fun findByLoginId(loginId: String): User? {

        return userJpaRepository.findByLoginId(loginId)?.toModel()
    }

    override fun save(user: User): User {
        return userJpaRepository.save(UserEntity.fromModel(user)).toModel()
    }
}
