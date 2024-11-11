package org.ssmartoffice.attendanceserver.controller.port

import org.ssmartoffice.attendanceserver.controller.response.AttendanceResponse

interface AttendanceService {

    fun createAttendance(userId: Long): AttendanceResponse
    fun getAttendanceByDate(userId: Long, month: String, date: String): List<AttendanceResponse>

}