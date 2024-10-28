package org.example.auth_module.user.controller

import org.example.auth_module.global.auth.CustomUserDetails
import org.example.auth_module.user.controller.port.UserService
import org.example.auth_module.user.domain.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/users")
class UserController(
    val userService: UserService
) {

    @PostMapping("/join")
    fun join(@RequestBody user: User): ResponseEntity<Map<String, Any>> {
        val response: MutableMap<String, Any> = HashMap()
        userService.addUser(user)
        response["msg"] = "회원가입에 성공하였습니다."
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{email}")
    fun getUser(@PathVariable email: String): ResponseEntity<User> {
        val user = userService.getUserByEmail(email)
        return ResponseEntity.ok(user)
    }

    @GetMapping("/me")
    fun myInfo(authentication: Authentication): ResponseEntity<User> {
        val user = (authentication.principal as CustomUserDetails).user
        return ResponseEntity.ok(user)
    }
}
