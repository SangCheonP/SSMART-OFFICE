package org.ssmartoffice.pointservice.controller.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.ssmartoffice.pointservice.domain.Transaction
import java.time.LocalDateTime

data class TransactionRequest(
    @field:NotBlank(message = "가맹점 이름을 입력해주세요.")
    val marketName: String,
    @field:NotNull(message = "거래 금액을 입력해주세요.")
    val amount: Int,
    val item: String?,
    val quantity: Int?,
    @field:NotNull(message = "거래 시간을 입력해주세요.")
    val transactionTime: LocalDateTime
) {
    fun toModel(): Transaction {
        return Transaction(
            marketName = marketName,
            amount = amount,
            transactionTime = transactionTime,
            item = item,
            quantity = quantity
        )
    }
}
