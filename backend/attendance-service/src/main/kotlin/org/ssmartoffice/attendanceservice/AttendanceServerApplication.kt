package org.ssmartoffice.attendanceservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class AttendanceServerApplication

fun main(args: Array<String>) {
    runApplication<AttendanceServerApplication>(*args)
}
