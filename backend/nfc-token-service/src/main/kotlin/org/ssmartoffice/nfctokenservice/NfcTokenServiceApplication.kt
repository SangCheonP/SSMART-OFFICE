package org.ssmartoffice.nfctokenservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class NfcTokenServiceApplication

fun main(args: Array<String>) {
	runApplication<NfcTokenServiceApplication>(*args)
}
