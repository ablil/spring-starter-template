package com.example.domain.accounts

import java.time.Instant
import org.slf4j.LoggerFactory

class AccountService(val repository: UserAccountRepository, val passwordEncoder: PasswordEncoder) {

    fun createAccount(request: CreateAccount): UserAccount {
        if (repository.exists(request.username, request.email)) {
            throw AccountAlreadyExists()
        }

        val created =
            repository.save(
                UserAccount(
                    username = request.username,
                    email = request.email,
                    info = UserInfo(firstName = request.firstName, lastName = request.lastName),
                    account =
                        AccountDetails(
                            password = passwordEncoder.encode(request.rawPassword),
                            status = AccountStatus.INACTIVE,
                            resetKey = null,
                            resetRequestedAt = null,
                            activationKey = "random", // TODO: set random token
                        ),
                )
            )
        logger.info("create new user account for username={}", created.username)
        return created
    }

    fun activateAccount(token: String) {
        val userAccount = repository.findByActivation(token) ?: throw AccountDoesNotExists()
        if (userAccount.account.status == AccountStatus.ACTIVE) {
            throw IllegalStateException("user account already activated")
        }
        if (userAccount.account.activationKey != token) {
            throw InvalidToken("invalid activation key")
        }

        repository.save(
            userAccount.copy(
                account =
                    userAccount.account.copy(status = AccountStatus.ACTIVE, activationKey = null)
            )
        )
        logger.info("activated user account for username={}", userAccount.username)
    }

    fun requestPasswordReset(identifier: UsernameOrEmail): Token? {
        val userAccount = repository.findByIdentifier(identifier)
        if (userAccount == null) {
            logger.info("user account not found for identifier={}", identifier)
            return null
        }

        val updated =
            repository.save(
                userAccount.copy(
                    account =
                        userAccount.account.copy(
                            status = AccountStatus.INACTIVE,
                            resetKey = "random", // TODO: generate token
                            resetRequestedAt = Instant.now(),
                        )
                )
            )
        logger.info("requested password reset for {}", identifier)
        // TODO: emit an event to send email or another approach instead of event
        return Token(requireNotNull(updated.account.resetKey))
    }

    fun resetPassword(token: Token, rawPassword: String) {
        val userAccount =
            repository.findByResetPasswordToken(token) ?: throw InvalidToken("invalid token")

        // TODO: check if key is expired

        if (passwordEncoder.match(rawPassword, userAccount.account.password)) {
            throw InvalidPassword("can NOT use the same old password")
        }

        repository.save(
            userAccount.copy(
                account =
                    userAccount.account.copy(
                        password = passwordEncoder.encode(rawPassword),
                        status = AccountStatus.ACTIVE,
                        resetKey = null,
                    )
            )
        )
        logger.info("reset user account password for {}", userAccount.username)
    }

    companion object {
        val logger = LoggerFactory.getLogger(AccountService::class.java)
    }
}

data class CreateAccount(
    val username: String,
    val email: String,
    val rawPassword: String,
    val firstName: String?,
    val lastName: String?,
)

class AccountAlreadyExists : RuntimeException()

class AccountDoesNotExists : RuntimeException()

class InvalidPassword(msg: String) : RuntimeException(msg)

class InvalidToken(msg: String) : RuntimeException(msg)

@JvmInline value class UsernameOrEmail(val identifier: String)

@JvmInline value class Token(val token: String)
