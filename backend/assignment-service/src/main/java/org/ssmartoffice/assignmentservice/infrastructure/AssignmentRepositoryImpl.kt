package org.ssmartoffice.userservice.infrastructure

import org.ssmartoffice.userservice.domain.Assignment
import org.ssmartoffice.userservice.service.port.AssignmentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class AssignmentRepositoryImpl(
    private val userJpaRepository: UserJpaRepository,
) : AssignmentRepository {
    override fun save(assignment: Assignment): Assignment {
        return userJpaRepository.save(AssignmentEntity.fromModel(assignment)).toModel()
    }

    override fun findById(id: Long): Assignment? {
        return userJpaRepository.findById(id)
            .map{it.toModel()}
            .orElse(null)
    }

    override fun findByEmail(email: String): Assignment? {
        return userJpaRepository.findByEmail(email).toModel()
    }

    override fun findAll(pageable: Pageable): Page<Assignment> {
        return userJpaRepository.findAll(pageable).map { userEntity -> userEntity.toModel() }
    }

    override fun findAllByIdIn(ids: List<Long>): List<Assignment> {
        return userJpaRepository.findAllByIdIn(ids).map { userEntity -> userEntity.toModel() }
    }

    override fun findMaxEmployeeNumberByYear(prefix: String): String? {
        return userJpaRepository.findTopByEmployeeNumberStartingWithOrderByEmployeeNumberDesc(prefix)
            ?.employeeNumber
    }

    override fun existsByEmail(adminEmail: String): Boolean {
        return userJpaRepository.existsByEmail(adminEmail)
    }
}