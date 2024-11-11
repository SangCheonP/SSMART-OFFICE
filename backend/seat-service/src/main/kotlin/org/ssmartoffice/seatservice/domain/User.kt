package org.ssmartoffice.seatservice.domain

import jakarta.validation.constraints.NotBlank

class User(
    val id: Long = 0,
    @field:NotBlank(message = "이름을 입력해주세요.")
    var name: String,
    @field:NotBlank(message = "직급을 입력해주세요.")
    var position: String,
    @field:NotBlank(message = "직무를 입력해주세요.")
    var duty: String,
)
