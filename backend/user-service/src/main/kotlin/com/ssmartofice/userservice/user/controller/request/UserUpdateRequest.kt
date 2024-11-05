package com.ssmartofice.userservice.user.controller.request

data class UserUpdateRequest(
    val email: String? = null,
    val password: String? = null,
    val name: String? = null,
    val position: String? = null,
    val duty: String? = null,
    val profileImageUrl: String? = null
)
