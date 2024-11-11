package org.ssmartoffice.seatservice.infratructure

import org.springframework.stereotype.Repository
import org.ssmartoffice.seatservice.domain.Seat
import org.ssmartoffice.seatservice.service.port.SeatRepository

@Repository
class SeatRepositoryImpl(
    private val seatJpaRepository: SeatJpaRepository
) : SeatRepository{
    override fun findAllByFloor(floor: Int): List<Seat> {
        return seatJpaRepository.findAllByFloor(floor).map{ seatEntity -> seatEntity.toModel() }
    }
}