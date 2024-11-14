package org.ssmartoffice.attendanceservice.controller.port

import org.ssmartoffice.attendanceservice.controller.response.AttendanceResponse

interface AttendanceService {
    fun createAttendance(userEmail: String): AttendanceResponse
    fun getAttendanceByDate(userEmail: String, month: String, date: String?): List<AttendanceResponse>
    fun getAttendanceByDate(userId: Long, month: String, date: String?): List<AttendanceResponse>

}