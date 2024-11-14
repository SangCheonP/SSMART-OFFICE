package org.ssmartoffice.attendanceservice.controller.response

import org.ssmartoffice.attendanceservice.domain.Attendance
import org.ssmartoffice.attendanceservice.domain.AttendanceType
import java.time.LocalDateTime

data class AttendanceResponse(
    val attendanceId: Long,
    val userId: Long,
    val attendanceType: AttendanceType,
    val attendanceTime: LocalDateTime?
) {
    companion object {
        fun fromModel(attendance: Attendance): AttendanceResponse {
            return AttendanceResponse(
                attendanceId = attendance.id ?: throw IllegalArgumentException("Attendance id is null"),
                userId = attendance.userId,
                attendanceType = attendance.type,
                attendanceTime = attendance.time
            )
        }
    }
}