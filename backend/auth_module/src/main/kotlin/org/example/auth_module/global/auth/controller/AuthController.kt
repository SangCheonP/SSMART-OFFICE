package org.example.auth_module.global.auth.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.example.auth_module.global.auth.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/auth")
class AuthController(
    val authService: AuthService
) {

    @PostMapping("/refresh")
    fun refreshToken(
        request: HttpServletRequest,
        response: HttpServletResponse,
        @RequestBody accessToken: Map<String, String>
    ): ResponseEntity<Map<String, Any>> {
        val token = authService.refreshToken(accessToken["accessToken"] ?: "")
        val result: MutableMap<String, Any> = HashMap()
        result["msg"] = "토큰 갱신에 성공했습니다."
        result["data"] = token

        return ResponseEntity.ok().body(result)
    }
}
