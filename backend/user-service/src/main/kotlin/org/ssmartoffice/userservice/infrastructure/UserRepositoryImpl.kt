package org.ssmartoffice.userservice.infrastructure

import org.ssmartoffice.userservice.domain.User
import org.ssmartoffice.userservice.service.port.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : UserRepository {
    override fun save(user: User): User {
        return userJpaRepository.save(UserEntity.fromModel(user)).toModel()
    }

    override fun findById(id: Long): User? {
        return userJpaRepository.findById(id)
            .map{it.toModel()}
            .orElse(null)
    }

    override fun findByEmail(email: String): User? {
        return userJpaRepository.findByEmail(email).toModel()
    }

    override fun findAll(pageable: Pageable): Page<User> {
        return userJpaRepository.findAll(pageable).map { userEntity -> userEntity.toModel() }
    }

    override fun findMaxEmployeeNumberByYear(prefix: String): String? {
        return userJpaRepository.findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc(prefix)
            ?.employeeNumber
    }

    override fun existsByEmail(adminEmail: String): Boolean {
        return userJpaRepository.existsByEmail(adminEmail)
    }
}