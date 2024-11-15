package org.ssmartoffice.fileservice.exception

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.lang.Nullable
import org.springframework.web.ErrorResponseException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.ssmartoffice.fileservice.const.errorcode.CommonErrorCode
import org.ssmartoffice.fileservice.const.errorcode.ErrorCode
import org.ssmartoffice.fileservice.const.errorcode.FileErrorCode
import org.ssmartoffice.fileservice.dto.ErrorResponse


private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {


    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<Any> {
        val errorCode: ErrorCode = CommonErrorCode.INVALID_PARAMETER
        return handleExceptionInternal(errorCode, e.message)
    }

    @ExceptionHandler(RestApiException::class)
    fun handleCustomException(e: RestApiException): ResponseEntity<Any> {
        val errorCode: ErrorCode = e.errorCode
        return handleExceptionInternal(errorCode)
    }

    @ExceptionHandler(Exception::class)
    fun handleServerException(ex: Exception?): ResponseEntity<Any> {
        val errorCode: ErrorCode = CommonErrorCode.INTERNAL_SERVER_ERROR
        ex?.printStackTrace()
        return handleExceptionInternal(errorCode)
    }



    private fun handleExceptionInternal(errorCode: ErrorCode): ResponseEntity<Any> {
        return ResponseEntity.status(errorCode.httpStatus)
            .body(makeErrorResponse(errorCode))
    }

    private fun makeErrorResponse(errorCode: ErrorCode): ErrorResponse {
        return ErrorResponse(
            status = errorCode.httpStatus.value(),
            error = errorCode.name,
            message = errorCode.message,
        )
    }

    private fun handleExceptionInternal(errorCode: ErrorCode, message: String?): ResponseEntity<Any> {
        return ResponseEntity.status(errorCode.httpStatus)
            .body(makeErrorResponse(errorCode, message))
    }

    private fun makeErrorResponse(errorCode: ErrorCode, message: String?): ErrorResponse {
        return ErrorResponse(
            status = errorCode.httpStatus.value(),
            error = errorCode.name,
            message = message,
        )
    }

    override fun handleMaxUploadSizeExceededException(
        ex: MaxUploadSizeExceededException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest
    ): ResponseEntity<Any> {
        val fileErrorCode = FileErrorCode.FILE_SIZE_EXCEEDED
        return handleExceptionInternal(ex, fileErrorCode)
    }

    private fun handleExceptionInternal(e: MaxUploadSizeExceededException, errorCode: ErrorCode): ResponseEntity<Any> {
        return ResponseEntity.status(errorCode.httpStatus)
            .body(makeErrorResponse(errorCode))
    }
}
