package org.ssmartoffice.attendanceserver.service

import org.springframework.stereotype.Service
import org.ssmartoffice.attendanceserver.controller.port.AttendanceService
import org.ssmartoffice.attendanceserver.controller.response.AttendanceResponse
import org.ssmartoffice.attendanceserver.infrastructure.AttendanceType
import java.time.LocalDateTime

@Service
class AttendanceServiceImpl: AttendanceService {

    override fun createAttendance(userId : Long): AttendanceResponse {
        return AttendanceResponse(1, 1, AttendanceType.START, LocalDateTime.now())
    }

    override fun getAttendanceByDate(userId : Long, month: String, date: String): List<AttendanceResponse> {
        return listOf(AttendanceResponse(1, 1, AttendanceType.END, LocalDateTime.now()))
    }


}