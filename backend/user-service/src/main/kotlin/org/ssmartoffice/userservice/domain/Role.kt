package org.ssmartoffice.userservice.domain

enum class Role {
    GUEST, USER, ADMIN;

    companion object {
        fun fromAuthority(authority: String): Role {
            val roleName = authority.removePrefix("ROLE_")
            return entries.find { it.name == roleName }
                ?: throw IllegalArgumentException("존재하지 않는 역할 $authority")
        }
    }

    fun isAdmin(): Boolean {
        return this == ADMIN
    }
}