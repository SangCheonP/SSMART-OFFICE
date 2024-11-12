package org.ssmartoffice.userservice.controller

import jakarta.validation.constraints.Email
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.ssmartoffice.userservice.global.dto.CommonResponse
import org.ssmartoffice.userservice.controller.port.UserService
import org.ssmartoffice.userservice.controller.request.UserLoginRequest
import org.ssmartoffice.userservice.controller.response.SeatUserResponse
import org.ssmartoffice.userservice.controller.response.UserLoginResponse


@RestController
@RequestMapping("api/v1/users/internal")
class InternalUserController(
    val userService: UserService
) {

    @GetMapping("/authentication")
    fun getIdAndRole(
        @RequestParam @Email(message = "유효한 이메일 주소를 입력해 주세요") email: String
    ): ResponseEntity<CommonResponse<UserLoginResponse?>> {
        val user = userService.findByUserEmail(email)
        return CommonResponse.ok(
            data = UserLoginResponse.fromModel(user),
            msg = "회원 아이디 및 역할 정보 조회 성공했습니다."
        )
    }

    @PostMapping("/login")
    fun selfLogin(
        @RequestBody request: UserLoginRequest
    ): ResponseEntity<CommonResponse<UserLoginResponse?>> {
        val user = userService.authenticateUser(request)
        return CommonResponse.ok(
            data = UserLoginResponse.fromModel(user),
            msg = "자체 로그인에 성공했습니다."
        )
    }

}