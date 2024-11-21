package org.ssmartoffice.attendanceservice.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.ssmartoffice.attendanceservice.client.UserServiceClient
import org.ssmartoffice.attendanceservice.controller.port.AttendanceService
import org.ssmartoffice.attendanceservice.controller.response.AttendanceResponse
import org.ssmartoffice.attendanceservice.domain.Attendance
import org.ssmartoffice.attendanceservice.global.const.errorcode.AttendanceErrorCode
import org.ssmartoffice.attendanceservice.global.exception.RestApiException
import org.ssmartoffice.attendanceservice.service.port.AttendanceRepository

private val logger = KotlinLogging.logger {}

@Service
class AttendanceServiceImpl(
    val userServiceClient :UserServiceClient,
    val attendanceRepository: AttendanceRepository
): AttendanceService {

    override fun createAttendance(userEmail: String): AttendanceResponse {
        val response = userServiceClient.getIdAndRole(userEmail)
        val userId = response.body?.data?.userId!!


        if(attendanceRepository.isExitedUserToday(userId)) {
            throw RestApiException(AttendanceErrorCode.ATTENDANCE_ALREADY_EXIST)
        }

        if(attendanceRepository.isEnteredUserToday(userId)) {
            return AttendanceResponse.fromModel(
                attendanceRepository.saveAttendance(
                    Attendance.leaveWork(userId)
                ))
        }

        return AttendanceResponse.fromModel(
            attendanceRepository.saveAttendance(
                Attendance.goToWork(userId)
            ))
    }

    override fun getAttendanceByDate(userEmail: String, month: String, date: String?): List<AttendanceResponse> {

        val response = userServiceClient.getIdAndRole(userEmail)
        val userId = response.body?.data?.userId!!

        if(date == null) {
            return attendanceRepository.getAttendanceByUserIdAndMonth(userId, month).map { AttendanceResponse.fromModel(it) }
        }

        return attendanceRepository.getAttendanceByUserIdAndDate(userId, month + date).map { AttendanceResponse.fromModel(it) }
    }

    override fun getAttendanceByDate(userId: Long, month: String, date: String?): List<AttendanceResponse> {

        if(date == null) {
            return attendanceRepository.getAttendanceByUserIdAndMonth(userId, month).map { AttendanceResponse.fromModel(it) }
        }

        return attendanceRepository.getAttendanceByUserIdAndDate(userId, month + date).map { AttendanceResponse.fromModel(it) }
    }
}
