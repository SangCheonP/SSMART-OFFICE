package org.ssmartoffice.attendanceservice.domain

import java.time.LocalDateTime

class Attendance(
    val id :Long? = null,
    val userId: Long,
    val type: AttendanceType,
    val time: LocalDateTime? = null
) {

    companion object {
        fun goToWork(userId: Long): Attendance {
            return Attendance(
                userId = userId,
                type = AttendanceType.START
            )
        }

        fun leaveWork(userId: Long): Attendance {
            return Attendance(
                userId = userId,
                type = AttendanceType.END
            )
        }
    }
}