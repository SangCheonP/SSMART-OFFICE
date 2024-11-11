package org.ssmartoffice.pointservice.controller.response

import org.ssmartoffice.pointservice.domain.Point
import java.time.LocalDateTime

data class PointInfoResponse(
    val id: Long = 0,
    val pointUsed: Int,
    val description: String,
    val currentPoint: Int,
    val useDate: LocalDateTime?
) {
    companion object {
        fun fromModel(point: Point): PointInfoResponse {
            return PointInfoResponse(
                id = point.id,
                pointUsed = point.pointUsed,
                description = point.description,
                currentPoint = point.currentPoint,
                useDate = point.useDate
            )
        }
    }
}
