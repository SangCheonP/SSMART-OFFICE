package org.ssmartoffice.seatservice.domain

enum class SeatStatus {
    IN_USE, //사용 중
    VACANT, //사용 가능
    UNAVAILABLE, //사용 불가
    NOT_OCCUPIED; //자리 비움

    fun isActive(): Boolean {
        return this == IN_USE || this == NOT_OCCUPIED
    }
}