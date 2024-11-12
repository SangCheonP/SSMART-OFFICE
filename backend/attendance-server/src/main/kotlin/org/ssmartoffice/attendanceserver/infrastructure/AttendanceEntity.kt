package org.ssmartoffice.attendanceserver.infrastructure

import jakarta.persistence.*
import org.ssmartoffice.attendanceserver.domain.Attendance
import org.ssmartoffice.attendanceserver.domain.AttendanceType
import java.time.LocalDateTime

@Entity
@Table(name = "attendances")
class AttendanceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id :Long? = null,
    val userId: Long,
    @Enumerated(value = EnumType.STRING)
    val type: AttendanceType,
) {
    companion object {
        fun fromModel(attendance: Attendance): AttendanceEntity {
            return AttendanceEntity(
                userId = attendance.userId,
                type = attendance.type
            )
        }
    }

    fun toModel(): Attendance {
        return Attendance(
            id = this.id,
            userId = this.userId,
            type = this.type,
            time = this.time
        )
    }


    @Column(nullable = false, updatable = false)
    private var time: LocalDateTime? = null
        private set

    @PrePersist
    fun onCreate() {
        val currentTime = LocalDateTime.now()
        time = currentTime
    }
}