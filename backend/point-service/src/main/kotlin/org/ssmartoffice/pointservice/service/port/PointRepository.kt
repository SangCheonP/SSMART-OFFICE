package org.ssmartoffice.pointservice.service.port

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository
import org.ssmartoffice.pointservice.domain.Point
import java.time.LocalDate

@Repository
interface PointRepository {
    fun findByUserIdAndUseDateBetween(userId: Long, startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<Point>
}