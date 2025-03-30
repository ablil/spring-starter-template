package com.example.users

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun existsByUsernameOrEmailIgnoreCase(username: String, email: String): Boolean

    fun findByUsernameIgnoreCase(username: String): User?

    fun findOneByActivationKey(key: String): User?
}
