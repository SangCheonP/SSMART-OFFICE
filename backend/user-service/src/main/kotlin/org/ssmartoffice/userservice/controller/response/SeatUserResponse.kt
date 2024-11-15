package org.ssmartoffice.userservice.controller.response

data class SeatUserResponse(
    val userId: Long = 0,
    var name: String,
    var position: String,
    var duty: String,
){
}
