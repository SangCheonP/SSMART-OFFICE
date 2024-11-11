package org.ssmartoffice.fileservice.const.errorcode

import org.springframework.http.HttpStatus

enum class FileErrorCode(override val httpStatus: HttpStatus, override val message: String) : ErrorCode {
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "파일을 찾을 수 없습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),
    FILE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "허용되지 않는 파일입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 크기가 초과되었습니다."),
    FILE_EXTENSION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "허용되지 않는 확장자입니다."),
    FILE_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "지원하지 않는 파일 형식입니다."),
    FILE_NOT_UPLOADABLE(HttpStatus.BAD_REQUEST, "파일을 업로드할 수 없습니다."),
}
