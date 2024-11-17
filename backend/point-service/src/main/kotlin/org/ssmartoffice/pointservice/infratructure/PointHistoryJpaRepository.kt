package org.ssmartoffice.pointservice.infratructure

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface PointHistoryJpaRepository : JpaRepository<PointHistoryEntity, Long> {

    fun findByUserIdAndTransactionTimeBetween(
        userId: Long,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        pageable: Pageable
    ): Page<PointHistoryEntity>

    fun findTop1ByUserIdOrderByCreatedDateTimeDesc(userId: Long): PointHistoryEntity?

}