package org.ssmartoffice.userservice.util

import org.ssmartoffice.userservice.service.port.UserRepository
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class EmployeeNumberGenerator(
    private val userRepository: UserRepository
) {
    fun generate(): String {
//        val currentYear = LocalDate.now().year.toString().substring(2) //2024->24
//        val lastNumber = userRepository.findMaxEmployeeNumberByYear("S$currentYear")?.substring(3)?.toIntOrNull()
//            ?: 0
//        val newSequence = lastNumber + 1
//        return "S$currentYear${String.format("%03d", newSequence)}"
        return ""
    }
}