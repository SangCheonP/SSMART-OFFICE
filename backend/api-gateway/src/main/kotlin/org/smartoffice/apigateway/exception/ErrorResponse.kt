package org.smartoffice.apigateway.exception

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.validation.FieldError

@JsonInclude(JsonInclude.Include.NON_EMPTY)
class ErrorResponse(
    val status: Int = 0,
    val error: String,
    val message: String?,
)
