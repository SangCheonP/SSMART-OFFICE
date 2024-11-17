package org.ssmartoffice.pointservice.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.ssmartoffice.pointservice.controller.port.PointService
import org.ssmartoffice.pointservice.domain.PointHistory
import org.ssmartoffice.pointservice.domain.Transaction
import org.ssmartoffice.pointservice.infratructure.PointHistoryHistoryRepositoryImpl
import java.time.LocalDate

@Service
class PointServiceImpl(
    private val pointHistoryRepository: PointHistoryHistoryRepositoryImpl,
) : PointService {

    override fun getPointsByDateRangeAndId(
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable,
        userId: Long
    ): Page<PointHistory> {
        return pointHistoryRepository.findByUserIdAndTransactionTimeBetween(userId, startDate, endDate, pageable)
    }

    override fun getMyPointBalance(userId: Long): Int {
        return pointHistoryRepository.findTop1ByUserIdOrderByCreatedDateTimeDesc(userId)
            ?.balance ?: 0
    }

    override fun createTransaction(userId: Long, transaction: Transaction): PointHistory {
        val balance = getMyPointBalance(userId)
        val pointHistory = PointHistory.createPointHistory(transaction, balance, userId)
        return pointHistoryRepository.save(pointHistory)
    }

}