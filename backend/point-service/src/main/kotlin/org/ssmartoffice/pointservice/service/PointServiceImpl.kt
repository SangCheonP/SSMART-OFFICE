package org.ssmartoffice.pointservice.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.ssmartoffice.pointservice.controller.port.PointService
import org.ssmartoffice.pointservice.domain.Point
import org.ssmartoffice.pointservice.infratructure.PointRepositoryImpl
import java.time.LocalDate

@Service
class PointServiceImpl(
    private val pointRepository: PointRepositoryImpl,
) : PointService {

    override fun getPointsByDateRangeAndId(
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable,
        userId: Long
    ): Page<Point> {
        return pointRepository.findByUserIdAndUseDateBetween(userId, startDate, endDate, pageable)
    }

}