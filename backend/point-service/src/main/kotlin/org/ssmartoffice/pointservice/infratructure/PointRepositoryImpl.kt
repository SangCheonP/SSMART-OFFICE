package org.ssmartoffice.pointservice.infratructure

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.ssmartoffice.pointservice.domain.Point
import org.ssmartoffice.pointservice.service.port.PointRepository
import java.time.LocalDate

@Repository
class PointRepositoryImpl(
    private val pointJpaRepository: PointJpaRepository
) : PointRepository {

    override fun findByUserIdAndUseDateBetween(
        userId: Long,
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable
    ): Page<Point> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(23, 59, 59)
        return pointJpaRepository.findByUserIdAndUseDateBetween(userId, startDateTime, endDateTime, pageable)
            .map { pointEntity -> pointEntity.toModel() }
    }

}