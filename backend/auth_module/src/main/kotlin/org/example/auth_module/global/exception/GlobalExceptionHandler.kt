package org.example.auth_module.global.exception

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.security.SignatureException
import lombok.extern.slf4j.Slf4j
import org.example.auth_module.global.exception.errorcode.CommonErrorCode
import org.example.auth_module.global.exception.errorcode.ErrorCode
import org.example.auth_module.global.exception.errorcode.UserErrorCode
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.util.stream.Collectors

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


    @ExceptionHandler(Exception::class)
    fun handleAllException(ex: Exception?): ResponseEntity<Any> {
        val errorCode: ErrorCode = CommonErrorCode.INTERNAL_SERVER_ERROR
        return handleExceptionInternal(errorCode)
    }

    @ExceptionHandler(SignatureException::class)
    fun handleSignatureException(ex: Exception?): ResponseEntity<Any> {
        val errorCode: ErrorCode = UserErrorCode.INVALID_TOKEN
        return handleExceptionInternal(errorCode)
    }

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJwtException(ex: Exception?): ResponseEntity<Any> {
        val errorCode: ErrorCode = UserErrorCode.EXPIRED_TOKEN_EXCEPTION
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

    private fun handleExceptionInternal(e: BindException, errorCode: ErrorCode): ResponseEntity<Any> {
        return ResponseEntity.status(errorCode.httpStatus)
            .body(makeErrorResponse(e, errorCode))
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
