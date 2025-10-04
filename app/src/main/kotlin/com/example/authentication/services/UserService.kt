package com.example.authentication.services

import com.example.authentication.rest.CreateOrUpdateUserDTO
import com.example.common.entities.DomainUser
import com.example.common.repositories.UserRepository
import jakarta.validation.constraints.NotBlank
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

        if (info.email != user.email && userRepository.existsByEmailIgnoreCase(info.email)) {
            throw EmailAlreadyUsed()
        }
        if (
            info.username != user.username &&
                userRepository.existsByUsernameIgnoreCase(info.username)
        ) {
            throw UsernameAlreadyUsed()
        }

        return userRepository
            .save(
                user.apply {
                    updateUserInfo(
                        email = info.email,
                        firstName = info.firstName,
                        lastName = info.lastName,
                        roles = info.roles,
                    )
                }
            )
            .also { logger.info("user info updated successfully") }
    }

    fun deleteUser(@NotBlank username: String) {
        val count: DomainUser? = userRepository.deleteByUsernameIgnoreCase(username)
        if (count == null) throw UserNotFound()
    }

    fun createUser(info: CreateOrUpdateUserDTO): DomainUser {
        if (userRepository.existsByEmailIgnoreCase(info.email)) {
            throw EmailAlreadyUsed()
        }
        if (userRepository.existsByUsernameIgnoreCase(info.username)) {
            throw UsernameAlreadyUsed()
        }

        return userRepository
            .save(
                DomainUser.newUser(
                        username = info.username,
                        email = info.email,
                        password =
                            passwordEncoder.encode(
                                RandomStringUtils.secure().nextAlphanumeric(DEFAULT_PASSWORD_LENGTH)
                            ),
                        roles = info.roles ?: emptySet(),
                        firstName = info.firstName,
                        lastName = info.lastName,
                    )
                    .apply { resetAccount(generateRandomKey()) }
            )
            .also { logger.info("user created successfully") }
    }
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@PreAuthorize("hasAuthority('ADMIN')")
annotation class AdminOnly
