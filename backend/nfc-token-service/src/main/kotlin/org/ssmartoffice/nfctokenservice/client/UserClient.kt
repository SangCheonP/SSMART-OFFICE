package org.ssmartoffice.nfctokenservice.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient(name = "user-service")
interface UserClient {

    @GetMapping("/api/v1/users/authentication")
    fun getAuthentication(email: String): String
}