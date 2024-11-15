package org.ssmartoffice.pointservice.domain

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

data class PointHistory(
    val id: Long = 0,
    @field:NotBlank(message = "사용자 정보가 없습니다.")
    val userId: Long,
    @field:NotBlank(message = "사용처가 없습니다.")
    val marketName: String,
    @field:NotNull(message = "거래 금액이 없습니다.")
    val amount: Int,
    @field:NotNull(message = "잔액이 없습니다.")
    val balance: Int,
    @field:NotNull(message = "거래 시간이 없습니다.")
    val transactionTime: LocalDateTime,
    val item: String? = null,
    val quantity: Int = 0
)
