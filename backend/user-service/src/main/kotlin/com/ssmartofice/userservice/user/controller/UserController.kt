package com.ssmartofice.userservice.user.controller

import com.ssmartofice.userservice.user.controller.port.UserService
import com.ssmartofice.userservice.user.domain.User
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController (
    val userService: UserService
){
    @GetMapping("/ping")
    fun check():ResponseEntity<Any>{
        return ResponseEntity.ok("pong");
    }

    @PostMapping
    fun join(@RequestBody @Valid user: User): ResponseEntity<Map<String, Any>> {
        val response: MutableMap<String, Any> = HashMap()
        userService.addUser(user)
        response["msg"] = "직원등록에 성공하였습니다."
        return ResponseEntity.ok(response)
    }
}