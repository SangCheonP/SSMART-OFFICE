package org.ssmartoffice.seatservice.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.ssmartoffice.seatservice.client.response.SeatUserResponse
import org.ssmartoffice.seatservice.global.dto.CommonResponse

@FeignClient(name = "USER-SERVICE")
interface UserServiceClient {

    @GetMapping("/api/v1/users/search")
    fun searchUsersByIds(@RequestParam userIds: List<Long>): ResponseEntity<CommonResponse<List<SeatUserResponse>>>

    @GetMapping("/api/v1/users/{userId}")
    fun searchUserById(@PathVariable userId: Long): ResponseEntity<CommonResponse<SeatUserResponse>>

    @GetMapping("/api/v1/users/{userId}/exists")
    fun existsById(@PathVariable userId: Long): ResponseEntity<CommonResponse<Boolean>>
}
