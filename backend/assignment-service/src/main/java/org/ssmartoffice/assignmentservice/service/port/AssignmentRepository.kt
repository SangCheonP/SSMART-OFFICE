package org.ssmartoffice.userservice.service.port

import org.ssmartoffice.userservice.domain.Assignment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
interface AssignmentRepository {
    fun save(assignment: Assignment): Assignment
    fun findById(id: Long): Assignment?
    fun findByEmail(email: String): Assignment?
    fun findAll(pageable: Pageable): Page<Assignment>
    fun findMaxEmployeeNumberByYear(s: String): String?
    fun existsByEmail(adminEmail: String): Boolean
    fun findAllByIdIn(ids: List<Long>): List<Assignment>
}