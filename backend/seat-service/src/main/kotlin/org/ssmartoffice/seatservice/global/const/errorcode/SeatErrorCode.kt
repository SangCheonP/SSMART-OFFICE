package org.ssmartoffice.seatservice.global.const.errorcode

import lombok.Getter
import org.springframework.http.HttpStatus

@Getter
enum class SeatErrorCode(override val httpStatus: HttpStatus, override val message: String) : ErrorCode {
    ACCESS_ADMIN_DENIED(HttpStatus.FORBIDDEN, "관리자만 접근 가능한 api입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    SEAT_NOT_FOUND(HttpStatus.NOT_FOUND, "좌석을 찾을 수 없습니다."),
    DUPLICATE_STATUS(HttpStatus.CONFLICT, "기존 좌석 해제 후 다시 앉아주세요"),
    OCCUPIED_BY_ANOTHER_USER(HttpStatus.CONFLICT, "다른 이용자가 사용 중입니다. 다른 좌석을 이용해주세요."),
    SEAT_UNAVAILABLE(HttpStatus.BAD_REQUEST, "사용 불가한 좌석입니다."),
    CONNECTION_FAIL(HttpStatus.SERVICE_UNAVAILABLE, "커넥션 중 에러가 발생했습니다."),
    ONLY_INUSE(HttpStatus.BAD_REQUEST, "사용 중일 때만 자리 비움이 가능합니다."),
    ONLY_ACTIVE(HttpStatus.BAD_REQUEST, "사용 중 또는 자리 비움 상태에서만 좌석 해제가 가능합니다."),
    ONLY_VACANT(HttpStatus.BAD_REQUEST, "사용자가 있을 때는 사용 불가 상태로 만들지 못합니다."),
}