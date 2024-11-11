package org.ssmartoffice.attendanceserver.infrastructure

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class AttendanceEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id :Long,
    val userId: Long,
    val type: AttendanceType,
) {

    @Column(nullable = false, updatable = false)
    private var time: LocalDateTime? = null
        private set

    @PrePersist
    fun onCreate() {
        val currentTime = LocalDateTime.now()
        time = currentTime
    }
}