package org.ssmartoffice.pointservice.controller

import org.springframework.stereotype.Component
import org.ssmartoffice.pointservice.controller.response.PointInfoResponse
import org.ssmartoffice.pointservice.domain.PointHistory

@Component
class PointHistoryMapper {

    fun toPointInfoResponse(pointHistory: PointHistory): PointInfoResponse {
        return PointInfoResponse(
            id = pointHistory.id,
            marketName = pointHistory.marketName,
            amount = pointHistory.amount,
            balance = pointHistory.balance,
            transactionTime = pointHistory.transactionTime,
            item = pointHistory.item,
            quantity = pointHistory.quantity
        )
    }

    fun toPointInfoResponse(balance: Int): PointInfoResponse {
        return PointInfoResponse(
            balance = balance
        )
    }
}