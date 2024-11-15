package org.ssmartoffice.pointservice.controller.port

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.ssmartoffice.pointservice.domain.PointHistory
import java.time.LocalDate

interface PointService {

    fun getPointsByDateRangeAndId(
        startDate: LocalDate,
        endDate: LocalDate,
        pageable: Pageable,
        userId: Long
    ): Page<PointHistory>

}