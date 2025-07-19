package com.example.users

import com.example.common.ApplicationException
import com.example.common.MailService
import com.example.common.SecurityUtils
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.Duration
import java.time.Instant
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.ResponseStatus

private const val DEFAULT_KEY_LENGTH = 32

@Service
class AccountService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val mailService: MailService?,
) {

    @Value("\${example.authentication.reset-password-key-validity-in-seconds:86400}")
    lateinit var resetKeyValidity: String

    val logger = getLogger()

    @Transactional
    fun registerUser(dto: RegistrationDTO) {
        val existingUser = userRepository.findByUsernameOrEmailIgnoreCase(dto.username, dto.email)
        if (existingUser != null) {
            throw EmailNotAllowed()
        }

        userRepository
            .saveAndFlush(
                DomainUser(
                    username = dto.username,
                    email = dto.email,
                    password = passwordEncoder.encode(dto.password),
                    activationKey = generateRandomKey(),
                )
            )
            .also { mailService?.sendAccountRegistrationEmail(it) }
        logger.info("created new user successfully")
    }

    @Transactional
    fun activateAccount(@NotBlank key: String) {
        val user = userRepository.findOneByActivationKey(key)

        if (user == null) {
            logger.warn("account activation attempt with invalid key")
            throw InvalidKey()
        }

        userRepository.save(user.apply { this.activate() })
            .also { mailService?.sendAccountActivationEmail(it) }
        logger.info("user account activated successfully {}", user.email)
    }

    fun requestPasswordReset(@Email email: String) {
        userRepository
            .findByEmailIgnoreCase(email)
            ?.takeUnless { it.disabled }
            ?.also { it.initPasswordReset(generateRandomKey()) }
            ?.let { userRepository.saveAndFlush(it) }
            ?.also { mailService?.sendPasswordResetLinkEmail(it) }
            ?: logger.warn("password reset request for unknown or disabled email {}", email)
    }

    fun finishPasswordReset(@NotBlank resetKey: String, @NotBlank newRawPassword: String) {
        val user =
            userRepository.findOneByResetKey(resetKey)?.takeUnless { it.disabled }
                ?: throw InvalidKey()
        if (passwordEncoder.matches(newRawPassword, user.password)) {
            throw UsingOldPassword()
        }

        if (
            user.resetDate
                ?.plus(Duration.ofSeconds(resetKeyValidity.toLong()))
                ?.isAfter(Instant.now()) == true
        ) {
            throw KeyExpired()
        }

        userRepository
            .saveAndFlush(
                user.also { it.finishResetPassword(passwordEncoder.encode(newRawPassword)) }
            )
            .also { mailService?.sendPasswordChangedEmail(it) }
        logger.info("password reset for user {} completed", user.username)
    }

    fun changePassword(@NotBlank currentPassword: String, @NotBlank newPassword: String) {
        val user = getCurrentUser()

        if (!passwordEncoder.matches(currentPassword, user.password)) {
            throw InvalidPassword("current password is invalid")
        }

        if (StringUtils.equals(currentPassword, newPassword)) {
            throw InvalidPassword("can NOT reuse the same password")
        }

        userRepository
            .saveAndFlush(user.apply { password = passwordEncoder.encode(newPassword) })
            .also { mailService?.sendPasswordChangedEmail(user) }
        logger.info("user password updated successfully")
    }

    @Transactional
    fun updateUserInfo(info: UserInfoDTO) {
        val user = findByLoging(SecurityUtils.currentUserLogin())

        if (user.email != info.email && userRepository.existsByEmailIgnoreCase(info.email)) {
            logger.warn("user attempted to update email to an already existing one")
            throw EmailNotAllowed()
        }

        val updatedUser =
            userRepository.saveAndFlush(
                user.apply {
                    firstName = info.firstName
                    lastName = info.lastName
                    email = requireNotNull(info.email)
                }
            )
        logger.info("user updated successfully {}", updatedUser)
    }

    private fun findByLoging(login: String): DomainUser {
        return userRepository.findByUsernameOrEmailIgnoreCase(login, login)
            ?: throw UserNotFound()
    }

    fun getCurrentUser(): DomainUser {
        val login = SecurityUtils.currentUserLogin()
        return userRepository.findByUsernameOrEmailIgnoreCase(login, login)
            ?: throw IllegalStateException("current user not found in the database, even though he is authenticated")
    }
}

fun Any.getLogger(): Logger = LoggerFactory.getLogger(this::class.java)

@ResponseStatus(HttpStatus.NOT_FOUND)
class UserNotFound : ApplicationException("User not found")

fun generateRandomKey(): String = RandomStringUtils.secure().nextAlphanumeric(DEFAULT_KEY_LENGTH)

@ResponseStatus(HttpStatus.BAD_REQUEST)
class EmailNotAllowed : ApplicationException("Email not allowed")

@ResponseStatus(HttpStatus.NOT_FOUND)
class InvalidKey : ApplicationException("Invalid key, no account account associated with it")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class KeyExpired : ApplicationException("Invalid key, expired")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class UsingOldPassword : ApplicationException("can NOT use old password")

@ResponseStatus(HttpStatus.BAD_REQUEST)
class InvalidPassword(msg: String? = null) : ApplicationException(
    if (StringUtils.isNotBlank(msg)) {
        "Invalid password, $msg"
    } else {
        "Invalid password"
    }
)