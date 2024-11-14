package org.ssmartoffice.attendanceservice.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AttendanceJpaRepository: JpaRepository<AttendanceEntity, Long> {

    @Query("SELECT * FROM attendances WHERE user_id = :userId AND DATE_FORMAT(time, '%Y%m%d') = :date", nativeQuery = true)
    fun getAttendanceByUserIdAndDate(userId: Long, date: String): List<AttendanceEntity>

    @Query("SELECT * FROM attendances WHERE user_id = :userId AND DATE_FORMAT(time, '%Y%m') = :month", nativeQuery = true)
    fun getAttendanceByUserIdAndMonth(userId: Long, month: String): List<AttendanceEntity>

    @Query("SELECT 1 FROM attendances WHERE user_id = :userId AND DATE_FORMAT(time, '%Y%m%d') = DATE_FORMAT(now(), '%Y%m%d') AND type = 'START' limit 1", nativeQuery = true)
    fun isEnteredUserToday(userId: Long): Int?

    @Query("SELECT 1 FROM attendances WHERE user_id = :userId AND DATE_FORMAT(time, '%Y%m%d') = DATE_FORMAT(now(), '%Y%m%d')  AND type = 'END' limit 1", nativeQuery = true)
    fun isExitedUserToday(userId: Long): Int?
}