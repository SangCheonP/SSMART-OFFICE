package org.ssmartoffice.userservice.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import org.ssmartoffice.userservice.global.dto.CommonResponse
import org.ssmartoffice.userservice.controller.port.UserService
import org.ssmartoffice.userservice.controller.request.PasswordUpdateRequest
import org.ssmartoffice.userservice.controller.request.UserRegisterRequest
import org.ssmartoffice.userservice.controller.request.UserUpdateRequest
import org.ssmartoffice.userservice.controller.response.UserInfoResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Positive
import org.springframework.data.domain.Page
import org.springframework.security.core.Authentication
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.ssmartoffice.userservice.controller.request.UserLoginRequest
import org.ssmartoffice.userservice.controller.response.SeatUserResponse
import org.ssmartoffice.userservice.controller.response.UserAuthenticationResponse
import org.ssmartoffice.userservice.domain.User

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    val userService: UserService,
    val userResponseMapper: UserResponseMapper
) {

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping
    fun join(
        @RequestBody @Valid userRegisterRequest: UserRegisterRequest
    ): ResponseEntity<CommonResponse<UserInfoResponse?>> {
        val registeredUser = userService.addUser(userRegisterRequest)
        val response = userResponseMapper.toUserInfoResponse(registeredUser)
        return CommonResponse.created(
            data = response,
            msg = "직원등록에 성공하였습니다."
        )
    }

    @GetMapping("/me")
    fun getMyInfo(authentication: Authentication): ResponseEntity<CommonResponse<UserInfoResponse?>> {
        val userId = authentication.principal as Long
        val user = userService.findUserByUserId(userId)
        val response = userResponseMapper.toUserInfoResponse(user)
        return CommonResponse.ok(
            data = response,
            msg = "내 정보 조회에 성공했습니다."
        )
    }

    @GetMapping("/{userId}")
    fun getEmployeeInfo(
        @Positive(message = "유효한 사용자 ID를 입력해주세요.")
        @PathVariable userId: Long,
        authentication: Authentication
    ): ResponseEntity<CommonResponse<UserInfoResponse?>> {
        val user = userService.findUserByUserIdWithAuth(userId, authentication)
        val response = userResponseMapper.toUserInfoResponse(user)
        return CommonResponse.ok(
            data = response,
            msg = "사원 조회에 성공했습니다."
        )
    }

    @GetMapping
    fun getEmployees(
        pageable: Pageable
    ): ResponseEntity<CommonResponse<Page<UserInfoResponse>?>> {
        val userPage: Page<User> = userService.getAllUsersByPage(pageable)
        val userInfoResponsePage: Page<UserInfoResponse> = userPage.map { user ->
            userResponseMapper.toUserInfoResponse(user)
        }
        return CommonResponse.ok(
            data = userInfoResponsePage,
            msg = "전체 사원 조회에 성공했습니다."
        )
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{userId}")
    fun updateEmployee(
        @RequestBody @Valid userUpdateRequest: UserUpdateRequest,
        @Positive(message = "유효한 사용자 ID를 입력해주세요.")
        @PathVariable userId: Long
    ): ResponseEntity<CommonResponse<UserInfoResponse?>> {
        val updatedUser = userService.updateUser(userId, userUpdateRequest)
        val response = userResponseMapper.toUserInfoResponse(updatedUser)
        return CommonResponse.ok(
            data = response,
            msg = "사원 수정에 성공했습니다."
        )
    }

    @PatchMapping("/me")
    fun updateMe(
        authentication: Authentication,
        @RequestBody @Valid userUpdateRequest: UserUpdateRequest
    ): ResponseEntity<CommonResponse<UserInfoResponse?>> {
        val userId = authentication.principal as Long
        val updatedUser = userService.updateUser(userId, userUpdateRequest)
        val response = userResponseMapper.toUserInfoResponse(updatedUser)
        return CommonResponse.ok(
            data = response,
            msg = "내 정보 수정에 성공했습니다."
        )
    }

    @PatchMapping("/me/password")
    fun updateMyPassword(
        authentication: Authentication,
        @RequestBody @Valid passwordUpdateRequest: PasswordUpdateRequest
    ): ResponseEntity<CommonResponse<Any?>> {
        val userId = authentication.principal as Long
        userService.updatePassword(userId, passwordUpdateRequest);
        return CommonResponse.ok(
            msg = "비밀번호 수정에 성공했습니다."
        )
    }

    @GetMapping("/search")
    fun searchUsersByIds(
        @RequestParam userIds: List<Long>
    ): ResponseEntity<CommonResponse<List<SeatUserResponse>?>> {
        logger.info { "userIds: $userIds" }
        val userList = userService.findAllByIds(userIds).map { user ->
            userResponseMapper.toSeatUserResponse(user)
        }
        logger.info { "Fetched user list: $userList" }
        return CommonResponse.ok(
            data = userList,
            msg = "좌석 조회에 필요한 회원 정보 조회에 성공했습니다."
        )
    }

    @GetMapping("/authentication")
    fun getIdAndRole(
        @RequestParam @Email(message = "유효한 이메일 주소를 입력해 주세요") email: String
    ): ResponseEntity<CommonResponse<UserAuthenticationResponse?>> {
        val user = userService.findByUserEmail(email)
        val response = userResponseMapper.toUserAuthenticationResponse(user)
        return CommonResponse.ok(
            data = response,
            msg = "회원 아이디 및 역할 정보 조회 성공했습니다."
        )
    }

    @PostMapping("/login")
    fun selfLogin(
        @RequestBody request: UserLoginRequest
    ): ResponseEntity<CommonResponse<UserAuthenticationResponse?>> {
        val user = userService.authenticateUser(request)
        val response = userResponseMapper.toUserAuthenticationResponse(user)
        return CommonResponse.ok(
            data = response,
            msg = "자체 로그인에 성공했습니다."
        )
    }

    @GetMapping("/{userId}/exists")
    fun existsById(
        @Positive @PathVariable userId: Long
    ): ResponseEntity<CommonResponse<Boolean?>> {
        return CommonResponse.ok(
            data = userService.existsById(userId),
            msg = "회원 존재 여부 체크에 성공했습니다."
        )
    }

    @GetMapping("/find/{keyword}")
    fun findUser(
        @PathVariable keyword: String,
        pageable: Pageable
    ): ResponseEntity<CommonResponse<Page<UserInfoResponse>?>>{
        return CommonResponse.ok(
            data = userService.findUser(keyword, pageable),
            msg = "사원 검색에 성공했습니다."
        )
    }

}