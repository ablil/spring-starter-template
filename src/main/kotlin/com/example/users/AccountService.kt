package com.example.users

import com.example.common.SecurityUtils
import java.time.Duration
import java.time.Instant
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.ResponseStatus

private const val DEFAULT_KEY_LENGTH = 10

@Service
class AccountService(val userRepository: UserRepository, val passwordEncoder: PasswordEncoder) {

    val logger = getLogger()

    @Transactional
    fun registerUser(dto: RegistrationDTO) {
        val existingUser = userRepository.findByUsernameOrEmailIgnoreCase(dto.username, dto.email)
        if (existingUser != null) {
            check(existingUser.disabled) { "user already exists" }
            userRepository.delete(existingUser)
            userRepository.flush()
        }

        userRepository.saveAndFlush(
            DomainUser(
                username = dto.username,
                email = dto.email,
                password = passwordEncoder.encode(dto.password),
                disabled = true,
                roles = emptySet(),
                firstName = null,
                lastName = null,
                activationKey = generateRandomKey(),
                resetKey = null,
                resetDate = Instant.now(),
            )
        )
        logger.info("created new user successfully")
    }

    fun activateAccount(key: String) {
        userRepository
            .findOneByActivationKey(key)
            ?.copy(activationKey = null, disabled = false)
            ?.let { userRepository.saveAndFlush(it) }
            ?.also { logger.info("user account {} activated", it.email) }
            ?: error("no user account associated with activation key")
    }

    fun requestPasswordReset(email: String) {
        userRepository
            .findByEmailIgnoreCase(email)
            ?.takeUnless { it.disabled }
            ?.copy(resetKey = generateRandomKey(), resetDate = Instant.now())
            ?.let { userRepository.saveAndFlush(it) } ?: error("user account for $email NOT found")
    }

    fun finishPasswordReset(resetKey: String, newRawPassword: String) {
        val user =
            userRepository.findOneByResetKey(resetKey)?.takeUnless { it.disabled }
                ?: error("no account with reset key found")
        checkOrThrow(!passwordEncoder.matches(newRawPassword, user.password)) {
            AccountResourceException("should NOT use old password")
        }

        check(user.resetDate?.plus(Duration.ofDays(1))?.isAfter(Instant.now()) == true) {
            "reset key expired"
        }

        userRepository.saveAndFlush(
            user.copy(
                resetKey = null,
                resetDate = null,
                password = passwordEncoder.encode(newRawPassword),
            )
        )
        logger.info("password reset completed for user {}", user.username)
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        val user =
            userRepository.findByUsernameOrEmailIgnoreCase(
                SecurityUtils.currentUserLogin(),
                SecurityUtils.currentUserLogin(),
            ) ?: error("user not found")

        check(passwordEncoder.matches(currentPassword, user.password)) {
            "current password is invalid"
        }
        check(!StringUtils.equals(currentPassword, newPassword)) {
            "new password should be different from old one"
        }

        userRepository.saveAndFlush(user.copy(password = passwordEncoder.encode(newPassword)))
        logger.info("user password updated successfully")
    }

    fun updateUserInfo(info: UserInfoDTO) {
        val currentLogin = SecurityUtils.currentUserLogin()
        val user =
            userRepository.findByUsernameOrEmailIgnoreCase(currentLogin, currentLogin)
                ?: logger.error("authenticated user '{}' is not persisted", currentLogin).run {
                    error("user not found")
                }

        check(!userRepository.existsByEmailIgnoreCase(info.email)) {
            "an exiting user with same email already exists"
        }

        userRepository.saveAndFlush(
            user.copy(
                firstName = info.firstName,
                lastName = info.lastName,
                email = requireNotNull(info.email),
            )
        )
        logger.info("user '{}' info updated successfully", currentLogin)
    }

    fun getCurrentUser(): DomainUser =
        userRepository.findByUsernameOrEmailIgnoreCase(
            SecurityUtils.currentUserLogin(),
            SecurityUtils.currentUserLogin(),
        )
            ?: logger.error("user is authenticated, but NO entity is found").run {
                error("user not found")
            }
}

fun Any.getLogger(): Logger = LoggerFactory.getLogger(this::class.java)

fun checkOrThrow(condition: Boolean, lazyException: () -> RuntimeException) {
    if (!condition) {
        throw lazyException.invoke()
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
class AccountResourceException(override val message: String) : RuntimeException(message)

fun generateRandomKey(): String = RandomStringUtils.secure().nextAlphanumeric(DEFAULT_KEY_LENGTH)
