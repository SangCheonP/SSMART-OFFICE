package org.ssmartoffice.pointservice.global.exception

import org.ssmartoffice.pointservice.global.dto.ErrorResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import lombok.extern.slf4j.Slf4j
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.HandlerMethodValidationException
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.ssmartoffice.pointservice.global.const.errorcode.CommonErrorCode
import org.ssmartoffice.pointservice.global.const.errorcode.ErrorCode
import org.ssmartoffice.pointservice.global.const.errorcode.PointErrorCode
import java.util.stream.Collectors

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
@Slf4j
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(RestApiException::class)
    fun handleCustomException(e: RestApiException): ResponseEntity<Any> {
        val errorCode: ErrorCode = e.errorCode
        return handleExceptionInternal(errorCode)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(e: IllegalArgumentException): ResponseEntity<Any> {
        val errorCode: ErrorCode = CommonErrorCode.INVALID_PARAMETER
        return handleExceptionInternal(errorCode, e.message)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleIllegalArgument(e: ConstraintViolationException): ResponseEntity<Any> {
        val errorCode: ErrorCode = CommonErrorCode.INVALID_PARAMETER
        return handleExceptionInternal(errorCode, e.message)
    }

    @ExceptionHandler(PointException::class)
    fun handleUserException(e: PointException): ResponseEntity<Any> {
        val errorCode: ErrorCode = e.errorCode
        return handleExceptionInternal(errorCode, e.message)
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDeniedException(ex: AuthorizationDeniedException?): ResponseEntity<Any> {
        val errorCode: ErrorCode = PointErrorCode.ACCESS_ADMIN_DENIED
        return handleExceptionInternal(errorCode)
    }

    @ExceptionHandler(Exception::class)
    fun handleServerException(ex: Exception?): ResponseEntity<Any> {
        val errorCode: ErrorCode = CommonErrorCode.INTERNAL_SERVER_ERROR
        if (ex != null) {
            ex.printStackTrace()
        }
        return handleExceptionInternal(errorCode)
    }

    override fun handleHttpMessageNotReadable(
        e: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any> {
        val commonErrorCode = CommonErrorCode.INVALID_JSON
        return handleExceptionInternal(commonErrorCode)
    }

    override fun handleMethodArgumentNotValid(
        e: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        logger.warn("handleIllegalArgument", e)
        val commonErrorCode = CommonErrorCode.INVALID_METHOD_ARG
        return handleExceptionInternal(e, commonErrorCode)
    }

    override fun handleHandlerMethodValidationException(
        e: HandlerMethodValidationException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        logger.warn("handleIllegalArgument", e)
        val commonErrorCode = CommonErrorCode.INVALID_METHOD_ARG
        return handleExceptionInternal(e, commonErrorCode)
    }

    private fun handleExceptionInternal(errorCode: ErrorCode): ResponseEntity<Any> {
        return ResponseEntity.status(errorCode.httpStatus)
            .body(makeErrorResponse(errorCode))
    }

    private fun handleExceptionInternal(e: BindException, errorCode: ErrorCode): ResponseEntity<Any> {
        return ResponseEntity.status(errorCode.httpStatus)
            .body(makeErrorResponse(e, errorCode))
    }

    private fun handleExceptionInternal(e: ResponseStatusException, errorCode: ErrorCode): ResponseEntity<Any> {
        return ResponseEntity.status(errorCode.httpStatus)
            .body(makeErrorResponse(e, errorCode))
    }

    private fun handleExceptionInternal(errorCode: ErrorCode, message: String?): ResponseEntity<Any> {
        return ResponseEntity.status(errorCode.httpStatus)
            .body(makeErrorResponse(errorCode, message))
    }

    private fun makeErrorResponse(errorCode: ErrorCode): ErrorResponse {
        return ErrorResponse(
            status = errorCode.httpStatus.value(),
            error = errorCode.name,
            message = errorCode.message,
        )
    }

    private fun makeErrorResponse(errorCode: ErrorCode, message: String?): ErrorResponse {
        return ErrorResponse(
            status = errorCode.httpStatus.value(),
            error = errorCode.name,
            message = message,
        )
    }

    private fun makeErrorResponse(error: ResponseStatusException, errorCode: ErrorCode): ErrorResponse {
        return ErrorResponse(
            status = errorCode.httpStatus.value(),
            error = errorCode.name,
            message = errorCode.message,
        )
    }

    private fun makeErrorResponse(e: BindException, errorCode: ErrorCode): ErrorResponse {
        val validationErrorList: List<ErrorResponse.ValidationError> = e.bindingResult
            .fieldErrors
            .stream()
            .map{ fieldError: FieldError ->
                ErrorResponse.ValidationError.of(
                    fieldError
                )
            }
            .collect(Collectors.toList())

        return ErrorResponse(
            status = errorCode.httpStatus.value(),
            error = errorCode.name,
            message = errorCode.message,
            errors = validationErrorList
        )
    }
}
