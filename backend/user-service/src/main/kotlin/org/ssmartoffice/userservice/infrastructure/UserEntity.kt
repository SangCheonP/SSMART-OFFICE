package org.ssmartoffice.userservice.infrastructure

import org.ssmartoffice.userservice.domain.Role
import org.ssmartoffice.userservice.domain.User
import org.ssmartoffice.userservice.domain.UserStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "users")
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["email"]),
        UniqueConstraint(columnNames = ["employee_number"])
    ]
)
class UserEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0,
    @Column(nullable = false, unique = true)
    val email: String,
    val password: String,
    val name: String,
    val position: String,
    val duty: String,
    val profileImageUrl: String,
    val role: Role = Role.USER,
    @Column(nullable = false, unique = true)
    val employeeNumber: String,
    val point: Int = 0,
    val status: UserStatus = UserStatus.OFF_DUTY,
    val deleted: Boolean = false,
    val phoneNumber: String?
) {
    @Column(nullable = false, updatable = false)
    private var createdDateTime: LocalDateTime? = null
        private set

    @Column(nullable = false)
    private var updatedDateTime: LocalDateTime? = null
        private set

    companion object {
        fun fromModel(user: User): UserEntity {
            return UserEntity(
                id = user.id,
                email = user.email,
                password = user.password,
                name = user.name,
                position = user.position,
                duty = user.duty,
                profileImageUrl = user.profileImageUrl,
                role = user.role,
                employeeNumber = user.employeeNumber,
                point = user.point,
                status = user.status,
                deleted = user.deleted,
                phoneNumber = user.phoneNumber
            )
        }
    }

    fun toModel(): User {
        return User(
            id = id,
            email = email,
            password = password,
            name = name,
            position = position,
            duty = duty,
            profileImageUrl = profileImageUrl,
            role = role,
            employeeNumber = employeeNumber,
            point = point,
            status = status,
            deleted = deleted,
            phoneNumber = phoneNumber
        )
    }

    @PrePersist
    fun onCreate() {
        val currentTime = LocalDateTime.now()
        createdDateTime = currentTime
        updatedDateTime = currentTime
    }

    @PreUpdate
    fun onUpdate() {
        updatedDateTime = LocalDateTime.now()
    }
}