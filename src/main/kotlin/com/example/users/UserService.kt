package com.example.users

import org.apache.commons.lang3.RandomStringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

private const val ACTIVATION_KEY_LENGTH = 10

@Service
class UserService(val userRepository: UserRepository, val passwordEncoder: PasswordEncoder) {

    val logger = getLogger()

    fun registerUser(dto: RegistrationDTO) {
        check(!userRepository.existsByUsernameOrEmailIgnoreCase(dto.username, dto.email)) {
            "user already exists"
        }

        userRepository.saveAndFlush(
            User(
                username = dto.username,
                email = dto.email,
                disabled = true,
                password = passwordEncoder.encode(dto.password),
                roles = emptySet(),
                firstName = null,
                lastName = null,
                activationKey = generateActivationKey(),
            )
        )
        logger.info("created new user successfully")
    }

    private fun generateActivationKey(): String =
        RandomStringUtils.secure().nextAlphanumeric(ACTIVATION_KEY_LENGTH)
}

fun Any.getLogger(): Logger = LoggerFactory.getLogger(this::class.java)
