package org.ssmartoffice.seatservice.infratructure

import org.springframework.stereotype.Repository
import org.ssmartoffice.seatservice.domain.Seat
import org.ssmartoffice.seatservice.service.port.SeatRepository

@Repository
class SeatRepositoryImpl(
    private val seatJpaRepository: SeatJpaRepository
) : SeatRepository {
    override fun findAllByFloor(floor: Int): List<Seat> {
        return seatJpaRepository.findAllByFloor(floor).map { seatEntity -> seatEntity.toModel() }
    }

    override fun findById(id: Long): Seat {
        return seatJpaRepository.findById(id).get().toModel()
    }

    override fun save(seat: Seat) {
        seatJpaRepository.save(SeatEntity.fromModel(seat))
    }

    override fun existsByUserIdAndIdNot(userId: Long, id: Long): Boolean {
        return seatJpaRepository.existsByUserIdAndIdNot(userId, id)
    }

    override fun findByUserId(userId: Long): Seat? {
        return seatJpaRepository.findByUserId(userId)?.toModel()
    }

}