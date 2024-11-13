package org.ssmartoffice.nfctokenservice.global.dto

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.validation.FieldError

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class ErrorResponse(
    val status: Int = 0,
    val error: String,
    val message: String?,
    val errors: List<ValidationError> = emptyList()
) {
    class ValidationError(
        val field: String,
        val message: String?
    ) {
        companion object {
            @JvmStatic
            fun of(fieldError: FieldError): ValidationError {
                return ValidationError(
                    field = fieldError.field,
                    message = fieldError.defaultMessage
                )
            }
        }
    }
}
