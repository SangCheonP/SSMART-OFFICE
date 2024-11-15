package org.ssmartoffice.pointservice.controller

import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.ssmartoffice.pointservice.controller.port.PointService
import org.ssmartoffice.pointservice.controller.request.TransactionRequest
import org.ssmartoffice.pointservice.controller.response.PointInfoResponse
import org.ssmartoffice.pointservice.domain.PointHistory
import org.ssmartoffice.pointservice.global.dto.CommonResponse
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/points")
class PointController(
    val pointService: PointService,
    val pointHistoryMapper: PointHistoryMapper,
) {

    @GetMapping("/history")
    fun getPointHistoryByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate,
        pageable: Pageable,
        authentication: Authentication
    ): ResponseEntity<CommonResponse<Page<PointInfoResponse>?>> {
        val userId = authentication.principal as Long
        val pointPage: Page<PointHistory> = pointService.getPointsByDateRangeAndId(startDate, endDate, pageable, userId)
        val pointInfoResponsePage: Page<PointInfoResponse> = pointPage.map { pointHistory ->
            pointHistoryMapper.toPointInfoResponse(pointHistory)
        }
        return CommonResponse.ok(
            msg = "기간별 포인트 내역 조회에 성공했습니다.",
            data = pointInfoResponsePage
        )
    }

    @GetMapping
    fun getPointBalance(
        authentication: Authentication
    ): ResponseEntity<CommonResponse<PointInfoResponse?>> {
        val userId = authentication.principal as Long
        val balance = pointService.getMyPointBalance(userId)
        val response = pointHistoryMapper.toPointInfoResponse(balance)
        return CommonResponse.ok(
            msg = "포인트 잔액 조회에 성공했습니다.",
            data = response
        )
    }

    @PostMapping
    fun createTransaction(
        @Valid @RequestBody transactionRequest: TransactionRequest,
        authentication: Authentication
    ): ResponseEntity<CommonResponse<PointInfoResponse?>> {
        val userId = authentication.principal as Long
        val transaction = transactionRequest.toModel()
        val newHistory=  pointService.createTransaction(userId, transaction)
        val response = pointHistoryMapper.toPointInfoResponse(newHistory)
        return CommonResponse.created(
            msg = "거래 내역 생성에 성공했습니다.",
            data = response
        )
    }
}