package com.example.users

import jakarta.validation.constraints.NotBlank
import java.time.Instant
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

private const val DEFAULT_PASSWORD_LENGTH = 32

@Service
@AdminOnly
class UserService(val userRepository: UserRepository, val passwordEncoder: PasswordEncoder) {

    val logger = getLogger()

    fun getAllUsers(request: Pageable): Page<DomainUser> = userRepository.findAll(request)

    fun getUser(@NotBlank username: String): DomainUser? =
        userRepository.findByUsernameIgnoreCase(username)

    fun updateUserInfo(@NotBlank username: String, info: CreateOrUpdateUserDTO): DomainUser? {
        val user = userRepository.findByUsernameIgnoreCase(username) ?: return null

        return userRepository
            .saveAndFlush(
                user.apply {
                    this.username = info.username
                    email = info.email
                    firstName = info.firstName
                    lastName = info.lastName
                    roles = info.roles ?: emptySet()
                }
            )
            .also { logger.info("user info updated successfully") }
    }

    fun deleteUser(@NotBlank username: String) {
        val count = userRepository.deleteByUsernameIgnoreCase(username)
        if (count == 0) throw UserNotFound()
    }

    fun createUser(info: CreateOrUpdateUserDTO): DomainUser {
        return userRepository
            .saveAndFlush(
                DomainUser(
                    username = info.username,
                    email = info.email,
                    password =
                        passwordEncoder.encode(
                            RandomStringUtils.secure().nextAlphanumeric(DEFAULT_PASSWORD_LENGTH)
                        ),
                    disabled = true,
                    roles = info.roles ?: emptySet(),
                    firstName = info.firstName,
                    lastName = info.lastName,
                    activationKey = null,
                    resetKey = generateRandomKey(),
                    resetDate = Instant.now(),
                )
            )
            .also { logger.info("user created successfully") }
    }
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ADMIN')")
annotation class AdminOnly
