package org.example.auth_module.global.exception.errorcode

import lombok.Getter
import org.springframework.http.HttpStatus

@Getter
enum class UserErrorCode(override val httpStatus: HttpStatus, override val message: String) : ErrorCode {
    NOT_FOUND_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "저장된 리프레시 토큰이 없습니다."),
    NOT_MATCH_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 일치하지 않습니다."),
    NO_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "해당 토큰이 존재하지 않습니다."),
    EXPIRED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED, "해당 토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "사용할 수 없는 토큰입니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "해당 api에 접근 권한이 없습니다."),
    NOT_MATCHED_REDIRECT_URI(HttpStatus.BAD_REQUEST, "Redirect URI가 맞지 않습니다."),
    DUPLICATE_APPROVAL_EXCEPTION(HttpStatus.CONFLICT, "이미 존재하는 가입승인입니다."),
    NOT_EXIST_APPROVAL_EXCEPTION(HttpStatus.NOT_FOUND, "존재하지 않는 가입승인입니다."),
    NOT_EXIST_USER_EXCEPTION(HttpStatus.NOT_FOUND, "해당 유저는 존재하지 않습니다."),
    NOT_EXIST_EVENT_EXCEPTION(HttpStatus.NOT_FOUND, "해당 이벤트는 존재하지 않습니다."),
    NOT_EXIST_ITEM_EXCEPTION(HttpStatus.NOT_FOUND, "해당 아이템은 존재하지 않습니다."),
    NOT_EXIST_POST_EXCEPTION(HttpStatus.NOT_FOUND, "해당 게시물은 존재하지 않습니다."),
    NOT_EXIST_REPLY_EXCEPTION(HttpStatus.NOT_FOUND, "해당 댓글은 존재하지 않습니다."),
    NOT_EXIST_ORDER_EXCEPTION(HttpStatus.NOT_FOUND, "해당 주문은 존재하지 않습니다."),
    NOT_EXIST_RANK_EXCEPTION(HttpStatus.NOT_FOUND, "해당 랭크포인트는 존재하지 않습니다."),
    NOT_EXIST_TIL_EXCEPTION(HttpStatus.NOT_FOUND, "해당 TIL은 존재하지 않습니다."),
    NOT_EXIST_TOTAL_POINT_EXCEPTION(HttpStatus.NOT_FOUND, "해당 TotalPoint은 존재하지 않습니다."),
    NOT_EXIST_RANDOM_POINT_EXCEPTION(HttpStatus.NOT_FOUND, "해당 RandomPoint은 존재하지 않습니다."),
    DUPLICATE_ATTENDANCE_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 출석 완료 처리되었습니다."),
    NO_POINT_EXCEPTION(HttpStatus.BAD_REQUEST, "가지고 있는 포인트가 부족합니다."),
    COMMIT_RESTRICT_EXCEPTION(HttpStatus.BAD_REQUEST, "더 이상 커밋 포인트를 얻을 수 없습니다."),
    NOT_MATCH_POST_EXCEPTION(HttpStatus.CONFLICT, "해당 게시물의 댓글이 아닙니다."),
    TIL_RESTRICTION_EXCEPTION(HttpStatus.UNAUTHORIZED, "TIL은 하루에 하나만 작성하실 수 있습니다.");

}