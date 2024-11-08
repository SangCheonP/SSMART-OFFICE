package org.example.auth_module.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.example.auth_module.global.auth.domain.CustomUserDetails
import org.example.auth_module.user.controller.port.UserService
import org.example.auth_module.user.domain.Test
import org.example.auth_module.user.domain.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Tag(name = "유저 API", description = "유저 관련 API")
@Validated
@RestController
@RequestMapping("/users")
class UserController(
    val userService: UserService
) {

    @Operation(summary = "직원 등록", description = "직원을 등록합니다.")
    @PostMapping("/join")
    fun join(@RequestBody @Valid user: User): ResponseEntity<Map<String, Any>> {
        val response: MutableMap<String, Any> = HashMap()
        userService.addUser(user)
        response["msg"] = "직원등록에 성공하였습니다."
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "이메일로 유저 조회", description = "이메일로 유저를 조회합니다.")
    @GetMapping("/{email}")
    fun getUser(
        @PathVariable
        @Email(message = "이메일 형식이 올바르지 않습니다.", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
        email: String): ResponseEntity<User> {
        val user = userService.getUserByEmail(email)
        return ResponseEntity.ok(user)
    }

    @Operation(summary = "내 정보 조회", description = "내 정보를 조회합니다.")
    @GetMapping("/me")
    fun myInfo(authentication: Authentication): ResponseEntity<User> {
        val user = (authentication.principal as CustomUserDetails).user
        return ResponseEntity.ok(user)
    }

    @GetMapping("/test")
    fun test() : ResponseEntity<Test>{
        val s = "{\n" +
                "    \"name\": \"test\",\n" +
                "    \"age\": 20\n" +
                "}"
        val mapper = ObjectMapper().registerModule(
            KotlinModule.Builder()
                .build()
        )
        val t : Test= mapper.readValue(s, Test::class.java)
        return ResponseEntity.ok(t)
    }


}
