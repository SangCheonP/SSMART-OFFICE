package org.ssmartoffice.pointservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PointServiceApplication

fun main(args: Array<String>) {
    runApplication<PointServiceApplication>(*args)
}
