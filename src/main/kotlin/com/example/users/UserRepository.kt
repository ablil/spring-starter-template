package com.example.users

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun existsByUsernameOrEmailIgnoreCase(username: String, email: String): Boolean

    fun findByUsernameOrEmailIgnoreCase(username: String, email: String): User?

    fun findByUsernameIgnoreCase(username: String): User?

    fun findByEmailIgnoreCase(email: String): User?

    fun existsByEmailIgnoreCase(email: String): Boolean

    fun findOneByActivationKey(key: String): User?

    fun findOneByResetKey(key: String): User?
}
