package org.ssmartoffice.pointservice.infratructure

import jakarta.persistence.*
import org.ssmartoffice.pointservice.domain.PointHistory
import java.time.LocalDateTime

@Entity(name = "point_history")
data class PointHistoryEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0,
    @Column(nullable = false)
    val userId: Long = 0,
    @Column(nullable = false)
    val marketName: String = "",
    @Column(nullable = false)
    val amount: Int = 0,
    @Column(nullable = false)
    val balance: Int = 0,
    @Column(nullable = false)
    val transactionTime: LocalDateTime = LocalDateTime.now(),
    val item: String? = null,
    val quantity: Int? = null
) {

    @Column(nullable = false, updatable = false)
    private var createdDateTime: LocalDateTime? = null
        private set

    @PrePersist
    fun onCreate() {
        val currentTime = LocalDateTime.now()
        createdDateTime = currentTime
    }


    companion object {
        fun fromModel(pointHistory: PointHistory): PointHistoryEntity {
            return PointHistoryEntity(
                id = pointHistory.id,
                userId = pointHistory.userId,
                marketName = pointHistory.marketName,
                amount = pointHistory.amount,
                balance = pointHistory.balance,
                transactionTime = pointHistory.transactionTime,
                item = pointHistory.item,
                quantity = pointHistory.quantity
            )
        }
    }

    fun toModel(): PointHistory {
        return PointHistory(
            id = id,
            userId = userId,
            marketName = marketName,
            amount = amount,
            balance = balance,
            transactionTime = transactionTime,
            item = item,
            quantity = quantity
        )
    }

}