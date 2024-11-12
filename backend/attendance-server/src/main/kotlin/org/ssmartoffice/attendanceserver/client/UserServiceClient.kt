package org.ssmartoffice.attendanceserver.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.ssmartoffice.attendanceserver.client.response.UserAuthenticationResponse
import org.ssmartoffice.attendanceserver.client.response.UserInfoResponse
import org.ssmartoffice.attendanceserver.global.dto.CommonResponse

@FeignClient(name = "user-service")
interface UserServiceClient {
    @GetMapping("/api/v1/users/internal/authentication")
    fun getIdAndRole(@RequestParam email: String): ResponseEntity<CommonResponse<UserAuthenticationResponse>>

}