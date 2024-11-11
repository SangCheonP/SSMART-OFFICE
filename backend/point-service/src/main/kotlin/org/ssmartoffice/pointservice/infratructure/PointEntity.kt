package org.ssmartoffice.pointservice.infratructure

import jakarta.persistence.*
import org.ssmartoffice.pointservice.domain.Point
import java.time.LocalDateTime

@Entity(name = "points")
class PointEntity(
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    val id: Long = 0,
    @Column(nullable = false)
    val userId: Long? = null,
    @Column(nullable = false)
    val pointUsed: Int = 0,
    @Column(nullable = false)
    val description: String = "",
    val currentPoint: Int = 0,
    val deleted: Boolean = false,
    val useDate: LocalDateTime? = null
) {

    companion object {
        fun fromModel(point: Point): PointEntity {
            return PointEntity(
                id = point.id,
                userId = point.userId,
                pointUsed = point.pointUsed,
                description = point.description,
                currentPoint = point.currentPoint,
                deleted = point.deleted,
                useDate = point.useDate
            )
        }
    }
    
    fun toModel(): Point {
        return Point(
            id = id,
            userId = userId,
            pointUsed = pointUsed,
            description = description,
            currentPoint = currentPoint,
            deleted = deleted,
            useDate = useDate
        )
    }
}