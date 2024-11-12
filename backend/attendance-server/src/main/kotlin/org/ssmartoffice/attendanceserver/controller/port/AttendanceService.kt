package org.ssmartoffice.attendanceserver.controller.port

import org.ssmartoffice.attendanceserver.controller.response.AttendanceResponse

interface AttendanceService {
    fun createAttendance(userEmail: String): AttendanceResponse
    fun getAttendanceByDate(userEmail: String, month: String, date: String?): List<AttendanceResponse>
    fun getAttendanceByDate(userId: Long, month: String, date: String?): List<AttendanceResponse>

}