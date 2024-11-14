package org.ssmartoffice.authenticationservice.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.ssmartoffice.authenticationservice.client.request.UserLoginRequest
import org.ssmartoffice.authenticationservice.client.response.UserAuthenticationResponse
import org.ssmartoffice.authenticationservice.global.dto.CommonResponse

@FeignClient(name = "USER-SERVICE")
interface UserServiceClient {

    @GetMapping("/api/v1/users/authentication")
    fun getIdAndRole(@RequestParam email: String): ResponseEntity<CommonResponse<UserAuthenticationResponse>>

    @PostMapping("/api/v1/users/login")
    fun selfLogin(@RequestBody request: UserLoginRequest): ResponseEntity<CommonResponse<UserAuthenticationResponse>>?
}