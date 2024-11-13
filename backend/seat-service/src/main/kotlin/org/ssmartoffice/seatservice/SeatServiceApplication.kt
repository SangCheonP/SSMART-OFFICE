package org.ssmartoffice.seatservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
class SeatServiceApplication

fun main(args: Array<String>) {
    runApplication<SeatServiceApplication>(*args)
}
