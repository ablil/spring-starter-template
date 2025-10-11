package com.example.domain.accounts

interface UserAccountRepository {
    fun exists(username: String, email: String): Boolean

    fun save(userAccount: UserAccount): UserAccount

    fun findByIdentifier(identifier: UsernameOrEmail): UserAccount?

    fun findActiveAccountByIdentifier(identifier: UsernameOrEmail): UserAccount?

    fun findByResetPasswordToken(token: Token): UserAccount?

    fun findByActivation(key: String): UserAccount?

    fun deleteAll(): Unit
}
