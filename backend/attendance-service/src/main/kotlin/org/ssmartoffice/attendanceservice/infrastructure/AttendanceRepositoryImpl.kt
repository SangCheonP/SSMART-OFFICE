package org.ssmartoffice.attendanceservice.infrastructure

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Repository
import org.ssmartoffice.attendanceservice.domain.Attendance
import org.ssmartoffice.attendanceservice.service.port.AttendanceRepository

val logger = KotlinLogging.logger {}

@Repository
class AttendanceRepositoryImpl(
    val attendanceJpaRepository: AttendanceJpaRepository
) : AttendanceRepository {

    override fun saveAttendance(attendance: Attendance) : Attendance {
        return attendanceJpaRepository.save(AttendanceEntity.fromModel(attendance)).toModel()
    }

    override fun getAttendanceByUserIdAndDate(userId: Long, date: String): List<Attendance> {
        return attendanceJpaRepository.getAttendanceByUserIdAndDate(userId, date).map { it.toModel() }
    }
    override fun getAttendanceByUserIdAndMonth(userId: Long, month: String): List<Attendance> {
        return attendanceJpaRepository.getAttendanceByUserIdAndMonth(userId, month)
            .map { it.toModel() }
    }

    override fun isEnteredUserToday(userId: Long): Boolean {
        logger.info { attendanceJpaRepository.isEnteredUserToday(userId) }
        return attendanceJpaRepository.isEnteredUserToday(userId) != null
    }

    override fun isExitedUserToday(userId: Long): Boolean {
        return attendanceJpaRepository.isExitedUserToday(userId) != null
    }
}

