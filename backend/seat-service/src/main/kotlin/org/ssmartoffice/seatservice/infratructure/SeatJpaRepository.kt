package org.ssmartoffice.seatservice.infratructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.ssmartoffice.seatservice.domain.SeatStatus

@Repository
interface SeatJpaRepository : JpaRepository<SeatEntity, Long> {
    fun findAllByFloor(floor: Int): List<SeatEntity>
    fun existsByUserIdAndStatus(userId: Long, status: SeatStatus): Boolean
}