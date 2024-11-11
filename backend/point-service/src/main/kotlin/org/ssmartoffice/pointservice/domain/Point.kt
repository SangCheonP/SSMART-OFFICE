package org.ssmartoffice.pointservice.domain

import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

class Point(
    val id: Long = 0,
    @field:NotBlank(message = "사용자 정보가 없습니다.")
    val userId: Long?,
    @field:NotBlank(message = "사용처가 없습니다.")
    val pointUsed: Int,
    @field:NotBlank(message = "사용 내역이 없습니다.")
    val description: String,
    @field:NotBlank(message = "남은 포인트가 없습니다.")
    val currentPoint: Int,
    val deleted: Boolean,
    val useDate: LocalDateTime?
)
