package org.ssmartoffice.seatservice.domain

enum class SeatStatus {
    IN_USE, //사용 중
    VACANT, //사용 가능
    UNAVAILABLE, //사용 불가
    NOT_OCCUPIED; //자리 비움

    fun isActive(): Boolean {
        return this == IN_USE || this == NOT_OCCUPIED
    }

    fun isInUse(): Boolean {
        return this == IN_USE
    }

    fun isUnavailable(): Boolean {
        return this == UNAVAILABLE
    }

    fun isVacant(): Boolean{
        return this== VACANT
    }

    fun isNotOccupied(): Boolean{
        return this== NOT_OCCUPIED
    }
}