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

private const val DEFAULT_KEY_LENGTH = 10

private const val USER_NOT_FOUND_ERROR_MSG = "user not found"

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
            check(existingUser.disabled) { "user already exists" }
            userRepository.delete(existingUser)
            userRepository.flush()
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

    fun activateAccount(@NotBlank key: String) {
        userRepository
            .findOneByActivationKey(key)
            ?.also { it.activate() }
            ?.let { userRepository.saveAndFlush(it) }
            ?.also { logger.info("user account {} activated", it.email) }
            ?.also { mailService?.sendAccountActivationEmail(it) }
            ?: error("no user account associated with activation key")
    }

    fun requestPasswordReset(@Email email: String) {
        userRepository
            .findByEmailIgnoreCase(email)
            ?.takeUnless { it.disabled }
            ?.also { it.initPasswordReset(generateRandomKey()) }
            ?.let { userRepository.saveAndFlush(it) }
            ?.also { mailService?.sendPasswordResetLinkEmail(it) }
            ?: error("user account for $email NOT found")
    }

    fun finishPasswordReset(@NotBlank resetKey: String, @NotBlank newRawPassword: String) {
        val user =
            userRepository.findOneByResetKey(resetKey)?.takeUnless { it.disabled }
                ?: error("no account with reset key found")
        checkOrThrow(!passwordEncoder.matches(newRawPassword, user.password)) {
            AccountResourceException("should NOT use old password")
        }

        check(
            user.resetDate
                ?.plus(Duration.ofSeconds(resetKeyValidity.toLong()))
                ?.isAfter(Instant.now()) == true
        ) {
            "reset key expired"
        }

        userRepository
            .saveAndFlush(
                user.also { it.finishResetPassword(passwordEncoder.encode(newRawPassword)) }
            )
            .also { mailService?.sendPasswordChangedEmail(it) }
        logger.info("password reset completed for user {}", user.username)
    }

    fun changePassword(@NotBlank currentPassword: String, @NotBlank newPassword: String) {
        val user =
            userRepository.findByUsernameOrEmailIgnoreCase(
                SecurityUtils.currentUserLogin(),
                SecurityUtils.currentUserLogin(),
            ) ?: error(USER_NOT_FOUND_ERROR_MSG)

        check(passwordEncoder.matches(currentPassword, user.password)) {
            "current password is invalid"
        }
        check(!StringUtils.equals(currentPassword, newPassword)) {
            "new password should be different from old one"
        }

        userRepository
            .saveAndFlush(user.apply { password = passwordEncoder.encode(newPassword) })
            .also { mailService?.sendPasswordChangedEmail(user) }
        logger.info("user password updated successfully")
    }

    fun updateUserInfo(info: UserInfoDTO) {
        val currentLogin = SecurityUtils.currentUserLogin()
        val user =
            userRepository.findByUsernameOrEmailIgnoreCase(currentLogin, currentLogin)
                ?: logger.error("authenticated user '{}' is not persisted", currentLogin).run {
                    error(USER_NOT_FOUND_ERROR_MSG)
                }

        check(!userRepository.existsByEmailIgnoreCase(info.email)) {
            "an exiting user with same email already exists"
        }

        userRepository.saveAndFlush(
            user.apply {
                firstName = info.firstName
                lastName = info.lastName
                email = requireNotNull(info.email)
            }
        )
        logger.info("user '{}' info updated successfully", currentLogin)
    }

    fun getCurrentUser(): DomainUser =
        userRepository.findByUsernameOrEmailIgnoreCase(
            SecurityUtils.currentUserLogin(),
            SecurityUtils.currentUserLogin(),
        )
            ?: logger.error("user is authenticated, but NO entity is found").run {
                error(USER_NOT_FOUND_ERROR_MSG)
            }
}

fun Any.getLogger(): Logger = LoggerFactory.getLogger(this::class.java)

fun checkOrThrow(condition: Boolean, lazyException: () -> RuntimeException) {
    if (!condition) {
        throw lazyException.invoke()
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class AccountResourceException(message: String) : ApplicationException(message)

fun generateRandomKey(): String = RandomStringUtils.secure().nextAlphanumeric(DEFAULT_KEY_LENGTH)
