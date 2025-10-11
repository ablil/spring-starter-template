package com.example.infra.accounts

import com.example.domain.accounts.AccountStatus
import com.example.domain.accounts.Token
import com.example.domain.accounts.UserAccount
import com.example.domain.accounts.UserAccountRepository
import com.example.domain.accounts.UsernameOrEmail
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAccountRepositoryAdapter :
    UserAccountRepository, JpaRepository<UserAccountEntity, String> {
    override fun exists(username: String, email: String): Boolean =
        this.existsByUsernameOrEmail(username, email)

    override fun save(userAccount: UserAccount): UserAccount =
        this.save(UserAccountEntity(userAccount)).toUserAccount()

    override fun findByIdentifier(identifier: UsernameOrEmail): UserAccount? =
        this.findByUsernameOrEmail(identifier.identifier, identifier.identifier)?.toUserAccount()

    override fun findActiveAccountByIdentifier(identifier: UsernameOrEmail): UserAccount? =
        this.findByIdentifier(identifier)?.takeIf { it.account.status == AccountStatus.ACTIVE }

    override fun findByResetPasswordToken(token: Token): UserAccount? =
        this.findByResetKey(token.token)?.toUserAccount()

    override fun findByActivation(key: String): UserAccount? =
        findByActivationKey(key)?.toUserAccount()

    fun existsByUsernameOrEmail(username: String, email: String): Boolean

    fun findByUsernameOrEmail(username: String, email: String): UserAccountEntity?

    fun findByResetKey(key: String): UserAccountEntity?

    fun findByActivationKey(key: String): UserAccountEntity?
}
