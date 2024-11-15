package org.ssmartoffice.pointservice.domain

import java.time.LocalDateTime

data class Transaction(
    val marketName: String,
    val amount: Int,
    val transactionTime: LocalDateTime,
    val item: String,
    val quantity: Int,
) {
    fun checkEnoughBalance(balance: Int): Boolean {
        return amount <= balance
    }
}