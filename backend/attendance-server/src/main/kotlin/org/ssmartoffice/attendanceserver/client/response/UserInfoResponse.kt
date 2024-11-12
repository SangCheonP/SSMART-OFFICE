package org.ssmartoffice.attendanceserver.client.response

import javax.management.relation.Role

class UserInfoResponse(
    val id: Long = 0,
    val employeeNumber: String,
    val email: String,
    val name: String,
    val position: String,
    val duty: String,
    val profileImageUrl: String,
    var role: Role,
    val phoneNumber:String?
)
