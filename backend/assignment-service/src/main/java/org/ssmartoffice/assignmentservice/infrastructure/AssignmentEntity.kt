package org.ssmartoffice.assignmentservice.infrastructure

import org.ssmartoffice.assignmentservice.domain.Assignment
import jakarta.persistence.*
import org.ssmartoffice.assignmentservice.domain.AssignmentType
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity(name = "assignments")
class AssignmentEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0,

    @Column(nullable = false)
    val userId: Long = 0,

    @Column(nullable = false, length = 255)
    val name: String = "",

    @Column(length = 8, nullable = false)
    val date: String = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    val type: AssignmentType = AssignmentType.TASK,

    @Lob
    val description: String = "",

    @Column(nullable = false)
    val completed: Boolean = false,

    @Column(nullable = false)
    val deleted: Boolean = false
) {

    @Column(nullable = false, updatable = false)
    private var createdDateTime: LocalDateTime? = null
        private set

    @Column(nullable = false)
    private var updatedDateTime: LocalDateTime? = null
        private set

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

    companion object {
        fun fromModel(assignment: Assignment): AssignmentEntity {
            return AssignmentEntity(
                id = assignment.id,
                userId = assignment.userId,
                name = assignment.name,
                date = assignment.date,
                type = assignment.type,
                description = assignment.description,
                completed = assignment.completed,
                deleted = assignment.deleted
            )
        }
    }

    fun toModel(): Assignment {
        return Assignment(
            id = id,
            userId = userId,
            name = name,
            date = date,
            type = type,
            description = description,
            completed = completed,
            deleted = deleted
        )
    }


}