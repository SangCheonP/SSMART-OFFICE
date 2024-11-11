package org.ssmartoffice.attendanceserver.controller.response

import org.ssmartoffice.attendanceserver.infrastructure.AttendanceType
import java.time.LocalDateTime

data class AttendanceResponse(
    val attendanceId: Long,
    val userId: Long,
    val attendanceType: AttendanceType,
    val attendanceTime: LocalDateTime
)