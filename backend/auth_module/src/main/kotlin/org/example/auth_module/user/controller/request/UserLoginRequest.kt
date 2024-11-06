package org.example.auth_module.user.controller.request

data class UserLoginRequest(
    var loginId: String,
    var password: String
)
