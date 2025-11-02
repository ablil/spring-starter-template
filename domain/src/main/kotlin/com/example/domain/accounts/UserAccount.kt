package com.example.domain.accounts

import java.time.Instant

data class UserAccount(
    val username: String,
    val email: String,
    val info: UserInfo,
    val account: AccountDetails,
)

data class UserInfo(val firstName: String?, val lastName: String?)

data class AccountDetails(
    val password: String,
    val status: AccountStatus,
    val activationKey: String?,
    val passwordReset: PasswordResetDTO?,
)

data class PasswordResetDTO(val key: String, val dueTo: Instant)

enum class AccountStatus {
    ACTIVE,
    INACTIVE,
}
