package org.ssmartoffice.authenticationservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@EnableFeignClients
@SpringBootApplication
private class AuthenticationServiceApplication

fun main(args: Array<String>) {
    runApplication<AuthenticationServiceApplication>(*args)
}
