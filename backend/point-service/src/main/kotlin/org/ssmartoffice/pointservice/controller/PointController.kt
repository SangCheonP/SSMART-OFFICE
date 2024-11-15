package org.ssmartoffice.pointservice.controller

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.ssmartoffice.pointservice.controller.port.PointService
import org.ssmartoffice.pointservice.controller.response.PointInfoResponse
import org.ssmartoffice.pointservice.domain.PointHistory
import org.ssmartoffice.pointservice.global.dto.CommonResponse
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/points")
class PointController(
    val pointService: PointService,
    val pointHistoryMapper: PointHistoryMapper
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
            pointHistoryMapper.toPointHistory(pointHistory)
        }
        return CommonResponse.ok(
            msg = "기간별 포인트 내역 조회에 성공했습니다.",
            data = pointInfoResponsePage
        )
    }
}