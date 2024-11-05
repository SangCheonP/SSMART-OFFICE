package com.ssmartofice.userservice.user.service.port

import com.ssmartofice.userservice.user.domain.User
import org.springframework.stereotype.Repository

@Repository
interface UserRepository {
    fun save(user: User): User
}