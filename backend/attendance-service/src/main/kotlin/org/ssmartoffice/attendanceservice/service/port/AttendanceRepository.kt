package org.ssmartoffice.attendanceservice.service.port

import org.ssmartoffice.attendanceservice.domain.Attendance

interface AttendanceRepository {
    fun saveAttendance(attendance: Attendance) : Attendance
    fun getAttendanceByUserIdAndDate(userId: Long, date: String): List<Attendance>
    fun getAttendanceByUserIdAndMonth(userId: Long, month: String): List<Attendance>
    fun isEnteredUserToday(userId: Long): Boolean

    fun isExitedUserToday(userId: Long): Boolean
}