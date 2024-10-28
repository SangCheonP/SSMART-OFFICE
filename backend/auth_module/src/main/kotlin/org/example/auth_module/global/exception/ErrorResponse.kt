package org.example.auth_module.global.exception

import com.fasterxml.jackson.annotation.JsonInclude
import lombok.Builder
import lombok.Getter
import lombok.RequiredArgsConstructor
import org.springframework.validation.FieldError

@Getter
class ErrorResponse(
    val status: Int = 0,
    val error: String,
    val message: String?,
    @JsonInclude(JsonInclude.Include.NON_EMPTY) val errors: List<ValidationError> = emptyList()
) {
    @Getter
    @Builder
    @RequiredArgsConstructor
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
