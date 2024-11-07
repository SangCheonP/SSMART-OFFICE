package org.ssmartoffice.authenticationservice.user.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.context.annotation.Configuration
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.ssmartoffice.authenticationservice.user.client.request.UserLoginRequest
import org.ssmartofice.userservice.global.dto.CommonResponse

@FeignClient(name = "user-service", url = "http://localhost:8080")
interface UserServiceClient {

    @GetMapping("/api/v1/users/internal/login")
    fun getIdAndRole(@RequestParam email: String): ResponseEntity<CommonResponse>

    @PostMapping("/api/v1/users/internal/login")
    fun selfLogin(@RequestBody request: UserLoginRequest): ResponseEntity<CommonResponse>?
}