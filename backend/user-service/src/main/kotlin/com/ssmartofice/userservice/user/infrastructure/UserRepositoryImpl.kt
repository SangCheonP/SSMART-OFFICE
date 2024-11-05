package com.ssmartofice.userservice.user.infrastructure

import com.ssmartofice.userservice.user.domain.User
import com.ssmartofice.userservice.user.service.port.UserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl (
    private val userJpaRepository: UserJpaRepository,
):UserRepository{
    override fun save(user: User): User {
        return userJpaRepository.save(UserEntity.fromModel(user)).toModel()
    }
}