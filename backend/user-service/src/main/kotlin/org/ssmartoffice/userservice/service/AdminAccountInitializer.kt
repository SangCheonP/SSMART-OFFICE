package org.ssmartoffice.userservice.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import org.ssmartoffice.userservice.service.port.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.ssmartoffice.userservice.domain.Role
import org.ssmartoffice.userservice.domain.User

@Component
class AdminAccountInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    @Value("\${app.admin.email}") private val adminEmail: String,
    @Value("\${app.admin.password}") private val adminPassword: String,
    @Value("\${app.admin.name}") private val adminName: String
) : ApplicationRunner {
    private val logger = KotlinLogging.logger {}

    override fun run(args: ApplicationArguments) {
        try {
            if (!userRepository.existsByEmail(adminEmail)) {
                val adminUser = User(
                    email = adminEmail,
                    password = passwordEncoder.encode(adminPassword),
                    name = adminName,
                    role = Role.ADMIN,
                    position = "주임",
                    duty = "인사팀",
                    profileImageUrl = "이미지 url",
                    employeeNumber = "S24000000"
                )
                userRepository.save(adminUser)
                logger.info { "관리자 계정이 성공적으로 생성되었습니다. (email: $adminEmail)" }
                logger.info { adminUser.password }
            } else {
                logger.info { "관리자 계정이 이미 존재합니다. (email: $adminEmail)" }
            }
        } catch (e: Exception) {
            logger.error { "관리자 계정 생성 중 오류가 발생했습니다: ${e.message}" }
            throw e
        }
    }
}