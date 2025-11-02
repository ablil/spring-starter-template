package com.example.persistence.accounts

import com.example.domain.accounts.AccountDetails
import com.example.domain.accounts.AccountStatus
import com.example.domain.accounts.PasswordResetDTO
import com.example.domain.accounts.UserAccount
import com.example.domain.accounts.UserInfo
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "users_account")
class UserAccountEntity(
    @Id val username: String,
    val email: String,
    val password: String,
    val firstName: String?,
    val lastName: String?,
    @Enumerated(EnumType.STRING) val status: AccountStatus,
    val activationKey: String?,
    val resetKey: String?,
    val resetDueTo: Instant?,
) {
    constructor(
        userAccount: UserAccount
    ) : this(
        username = userAccount.username,
        email = userAccount.email,
        password = userAccount.account.password,
        firstName = userAccount.info.firstName,
        lastName = userAccount.info.lastName,
        status = userAccount.account.status,
        activationKey = userAccount.account.activationKey,
        resetKey = userAccount.account.passwordReset?.key,
        resetDueTo = userAccount.account.passwordReset?.dueTo,
    )

    fun toUserAccount(): UserAccount {
        return UserAccount(
            username = this.username,
            email = this.email,
            info = UserInfo(firstName = this.firstName, lastName = this.lastName),
            account =
                AccountDetails(
                    password = this.password,
                    status = this.status,
                    activationKey = this.activationKey,
                    passwordReset =
                        if (resetKey != null && resetDueTo != null) {
                            PasswordResetDTO(resetKey, resetDueTo)
                        } else {
                            null
                        },
                ),
        )
    }
}
