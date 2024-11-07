package org.ssmartoffice.userservice.user.controller

import org.ssmartoffice.userservice.global.dto.CommonResponse
import org.ssmartoffice.userservice.user.controller.port.UserService
import org.ssmartoffice.userservice.user.controller.request.PasswordUpdateRequest
import org.ssmartoffice.userservice.user.controller.request.UserRegisterRequest
import org.ssmartoffice.userservice.user.controller.request.UserUpdateRequest
import org.ssmartoffice.userservice.user.controller.response.UserInfoResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import org.springframework.security.core.Authentication
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    val userService: UserService
) {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    fun join(
        @RequestBody @Valid userRegisterRequest: UserRegisterRequest
    ): ResponseEntity<CommonResponse> {
        val registeredUser = userService.addUser(userRegisterRequest)
        return CommonResponse.created(
            data = UserInfoResponse.fromModel(registeredUser),
            msg = "직원등록에 성공하였습니다."
        )
    }

    @GetMapping("/me")
    fun getMyInfo(authentication: Authentication): ResponseEntity<CommonResponse> {
        val userId = authentication.principal as Long
        val user = userService.findUserByUserId(userId)
        return CommonResponse.ok(
            data = UserInfoResponse.fromModel(user),
            msg = "내 정보 조회에 성공했습니다."
        )
    }

    @GetMapping("/{userId}")
    fun getEmployeeInfo(
        @Positive(message = "유효한 사용자 ID를 입력해주세요.")
        @PathVariable userId: Long
    ): ResponseEntity<CommonResponse> {
        val user = userService.findUserByUserId(userId)
        return CommonResponse.ok(
            data = UserInfoResponse.fromModel(user),
            msg = "사원 조회에 성공했습니다."
        )
    }

    @GetMapping
    fun getEmployees(
        pageable: Pageable
    ): ResponseEntity<CommonResponse> {
        val userList = userService.getAllUsersByPage(pageable).map { user ->
            UserInfoResponse.fromModel(user)
        }
        return CommonResponse.ok(
            data = userList,
            msg = "전체 사원 조회에 성공했습니다."
        )
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{userId}")
    fun updateEmployee(
        @RequestBody @Valid userUpdateRequest: UserUpdateRequest,
        @Positive(message = "유효한 사용자 ID를 입력해주세요.")
        @PathVariable userId: Long
    ): ResponseEntity<CommonResponse> {
        val updatedUser = userService.updateUser(userId, userUpdateRequest);
        return CommonResponse.ok(
            data = UserInfoResponse.fromModel(updatedUser),
            msg = "사원 수정에 성공했습니다."
        )
    }

    @PatchMapping("/me")
    fun updateMe(
        authentication: Authentication,
        @RequestBody @Valid userUpdateRequest: UserUpdateRequest
    ): ResponseEntity<CommonResponse> {
        val userId = authentication.principal as Long
        val updatedUser = userService.updateUser(userId, userUpdateRequest);
        return CommonResponse.ok(
            data = UserInfoResponse.fromModel(updatedUser),
            msg = "내 정보 수정에 성공했습니다."
        )
    }

    @PatchMapping("/me/password")
    fun updateMyPassword(
        authentication: Authentication,
        @RequestBody @Valid passwordUpdateRequest: PasswordUpdateRequest
    ): ResponseEntity<CommonResponse> {
        val userId = authentication.principal as Long
        userService.updatePassword(userId, passwordUpdateRequest);
        return CommonResponse.ok(
            msg = "비밀번호 수정에 성공했습니다."
        )
    }


}