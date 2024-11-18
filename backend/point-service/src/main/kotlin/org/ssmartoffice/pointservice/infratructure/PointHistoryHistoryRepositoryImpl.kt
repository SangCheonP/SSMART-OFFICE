package org.ssmartoffice.pointservice.infratructure

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.ssmartoffice.pointservice.domain.PointHistory
import org.ssmartoffice.pointservice.service.port.PointHistoryRepository
import java.time.LocalDate

@Repository
class PointHistoryHistoryRepositoryImpl(
    private val pointHistoryJpaRepository: PointHistoryJpaRepository
) : PointHistoryRepository {

    override fun findByUserIdAndTransactionTimeBetween(
        userId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<PointHistory> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        return pointHistoryJpaRepository.findByUserIdAndTransactionTimeBetween(userId, startDateTime, endDateTime, pageable)
            .map { pointHistoryEntity -> pointHistoryEntity.toModel() }
    }

    override fun findTop1ByUserIdOrderByCreatedDateTimeDesc(userId: Long): PointHistory? {
        return pointHistoryJpaRepository.findTop1ByUserIdOrderByCreatedDateTimeDesc(userId)
            ?.toModel()
    }

    override fun save(pointHistory: PointHistory): PointHistory {
        val savedEntity = pointHistoryJpaRepository.save(PointHistoryEntity.fromModel(pointHistory))
        return savedEntity.toModel()
    }

}