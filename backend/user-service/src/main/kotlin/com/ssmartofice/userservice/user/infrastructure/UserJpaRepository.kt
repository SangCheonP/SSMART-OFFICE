package com.ssmartofice.userservice.user.infrastructure

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository :JpaRepository<UserEntity, Long>{
}