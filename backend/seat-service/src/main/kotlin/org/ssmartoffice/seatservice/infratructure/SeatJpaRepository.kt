package org.ssmartoffice.seatservice.infratructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SeatJpaRepository : JpaRepository<SeatEntity, Long> {
    fun findAllByFloor(floor: Int): List<SeatEntity>
}