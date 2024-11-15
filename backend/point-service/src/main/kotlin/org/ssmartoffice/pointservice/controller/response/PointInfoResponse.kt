package org.ssmartoffice.pointservice.controller.response

import java.time.LocalDateTime

data class PointInfoResponse(
    val id: Long = 0,
    val marketName: String,
    val amount: Int,
    val balance: Int,
    val transactionTime: LocalDateTime,
    val item: String? = null,
    val quantity: Int = 0
)