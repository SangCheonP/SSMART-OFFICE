package org.ssmartoffice.pointservice.controller.response

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class PointInfoResponse(
    val id: Long? = null,
    val marketName: String? = null,
    val amount: Int? = null,
    val balance: Int? = null,
    val transactionTime: LocalDateTime? = null,
    val item: String? = null,
    val quantity: Int? = null,
)