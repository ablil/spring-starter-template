package com.example.users

import java.time.Instant
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@AdminOnly
class UserService(val userRepository: UserRepository) {

    val logger = getLogger()

    fun getAllUsers(request: Pageable): Page<DomainUser> = userRepository.findAll(request)

    fun getUser(username: String): DomainUser? = userRepository.findByUsernameIgnoreCase(username)

    fun updateUserInfo(username: String, info: CreateOrUpdateUserDTO): DomainUser? {
        val user = userRepository.findByUsernameIgnoreCase(username) ?: return null

        return userRepository
            .saveAndFlush(
                user.copy(
                    username = info.username,
                    email = info.email,
                    firstName = info.firstName,
                    lastName = info.lastName,
                    roles = info.roles ?: emptySet(),
                )
            )
            .also { logger.info("user info updated successfully") }
    }

    fun deleteUser(username: String) {
        val count = userRepository.deleteByUsernameIgnoreCase(username)
        if (count == 0) error("user not found")
    }

    fun createUser(info: CreateOrUpdateUserDTO): DomainUser {
        return userRepository
            .saveAndFlush(
                DomainUser(
                    username = info.username,
                    email = info.email,
                    password = "{noop}temporarypassword",
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
