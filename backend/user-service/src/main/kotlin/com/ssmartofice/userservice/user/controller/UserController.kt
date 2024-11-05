package com.ssmartofice.userservice.user.controller

import com.ssmartofice.userservice.global.const.successcode.SuccessCode
import com.ssmartofice.userservice.global.dto.CommonResponse
import com.ssmartofice.userservice.global.jwt.JwtUtil
import com.ssmartofice.userservice.user.controller.port.UserService
import com.ssmartofice.userservice.user.controller.response.UserInfoResponse
import com.ssmartofice.userservice.user.domain.User
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    val userService: UserService,
    val jwtUtil: JwtUtil
) {
    @GetMapping("/ping")
    fun check(): ResponseEntity<Any> {
        return ResponseEntity.ok("pong");
    }

    @PostMapping
    fun join(
        @RequestHeader("Authorization") token: String,
        @RequestBody @Valid user: User): ResponseEntity<CommonResponse> {
        userService.addUser(user)
        return ResponseEntity.ok(
            CommonResponse.builder()
                .status(SuccessCode.CREATED.getValue())
                .message("직원등록에 성공하였습니다.")
                .build()
        )
    }

    @GetMapping("/me")
    fun getMyInfo(@RequestHeader("Authorization") token: String): ResponseEntity<CommonResponse> {
        val user = jwtUtil.getUserByToken(token)
        return ResponseEntity.ok(
            CommonResponse.builder()
                .status(SuccessCode.OK.getValue())
                .data(UserInfoResponse.fromModel(user))
                .message("내 정보 조회에 성공했습니다.")
                .build()
        )
    }

    @GetMapping("/{userId}")
    fun getEmployeeInfo(
        @RequestHeader("Authorization") token: String,
        @PathVariable userId: Long): ResponseEntity<CommonResponse> {
        val user = userService.findUserByUserId(userId)
        return ResponseEntity.ok(
            CommonResponse.builder()
                .status(SuccessCode.OK.getValue())
                .data(UserInfoResponse.fromModel(user))
                .message("사원 조회에 성공했습니다.")
                .build()
        )
    }

    @GetMapping()
    fun getEmployees(
        @RequestHeader("Authorization") token: String,
        pageable: Pageable): ResponseEntity<CommonResponse> {
        val userList = userService.getAllUsersByPage(pageable).map{
            user->UserInfoResponse.fromModel(user)
        }
        return ResponseEntity.ok(
            CommonResponse.builder()
                .status(SuccessCode.OK.getValue())
                .data(userList)
                .message("전체 사원 조회에 성공했습니다.")
                .build()
        )
    }

    @PatchMapping("/{userId}")
    fun updateEmployee(
        @RequestHeader("Authorization") token: String,
        @RequestBody @Valid user: User, @PathVariable userId: Long): ResponseEntity<CommonResponse> {
        val updatedUser = userService.updateUser(userId, user);
        return ResponseEntity.ok(
            CommonResponse.builder()
                .status(SuccessCode.OK.getValue())
                .data(updatedUser)
                .message("사원 수정에 성공했습니다.")
                .build()
        )
    }


}