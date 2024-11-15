package org.ssmartoffice.seatservice.infratructure

import jakarta.persistence.*
import org.ssmartoffice.seatservice.domain.Seat
import org.ssmartoffice.seatservice.domain.SeatStatus
import java.time.LocalDateTime

@Entity(name = "seats")
@Table(
    name = "seats",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["user_id"]),
        UniqueConstraint(columnNames = ["info"])
    ]
)
class SeatEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0,
    @Column(unique = true)
    val userId: Long? = null,
    @Column(nullable = false, unique = true)
    val info: String = "",
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: SeatStatus = SeatStatus.VACANT,
    val floor: Int = 1,
) {

    companion object {
        fun fromModel(seat: Seat): SeatEntity {
            return SeatEntity(
                id = seat.id,
                userId = seat.userId,
                info = seat.info,
                status = seat.status,
                floor = seat.floor
            )
        }
    }

    @Column(nullable = false)
    private var updatedDateTime: LocalDateTime? = null
        private set

    @PreUpdate
    fun onUpdate() {
        updatedDateTime = LocalDateTime.now()
    }

    fun toModel(): Seat {
        return Seat(
            id = id,
            userId = userId,
            info = info,
            status = status,
            floor = floor,
            updatedDateTime = updatedDateTime
        )
    }
}