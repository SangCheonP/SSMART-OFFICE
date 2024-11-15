package org.ssmartoffice.pointservice.controller

import org.springframework.stereotype.Component
import org.ssmartoffice.pointservice.controller.response.PointInfoResponse
import org.ssmartoffice.pointservice.domain.PointHistory

@Component
class PointHistoryMapper {
    fun toPointHistory(pointHistory: PointHistory): PointInfoResponse {
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
}