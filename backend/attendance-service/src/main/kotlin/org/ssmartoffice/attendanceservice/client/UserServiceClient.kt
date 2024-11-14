package org.ssmartoffice.attendanceservice.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.ssmartoffice.attendanceservice.client.response.UserAuthenticationResponse
import org.ssmartoffice.attendanceservice.global.dto.CommonResponse

@FeignClient(name = "user-service")
interface UserServiceClient {
    @GetMapping("/api/v1/users/authentication")
    fun getIdAndRole(@RequestParam email: String): ResponseEntity<CommonResponse<UserAuthenticationResponse>>

}