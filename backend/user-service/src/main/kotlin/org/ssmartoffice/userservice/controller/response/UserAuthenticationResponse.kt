package org.ssmartoffice.userservice.controller.response

import org.ssmartoffice.userservice.domain.Role

class UserAuthenticationResponse(
    val userId: Long = 0,
    var role: Role
)