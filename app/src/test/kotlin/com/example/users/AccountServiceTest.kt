package com.example.users

import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.springframework.security.authentication.TestingAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.factory.PasswordEncoderFactories

class AccountServiceTest {
    val repository = mock<UserRepository>()

    val service =
        AccountService(
            userRepository = repository,
            passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder(),
            mailService = mock(),
        )

    @Test
    fun `should throw exception given an existing user with same email when updating user details`() {
        SecurityContextHolder.setContext(
            SecurityContextHolder.createEmptyContext().apply {
                authentication = TestingAuthenticationToken("dummy", "dummy")
            }
        )

        whenever(repository.findByUsernameOrEmailIgnoreCase(any(), any()))
            .thenReturn(
                DomainUser(
                    username = "johndoe",
                    email = "first@example.com",
                    password = "supersecurepassword",
                    disabled = false,
                    roles = emptySet(),
                    firstName = null,
                    lastName = null,
                    activationKey = null,
                    resetKey = null,
                    resetDate = null,
                    id = 1,
                )
            )
        whenever(repository.existsByEmailIgnoreCase("second@example.com")).thenReturn(true)

        org.junit.jupiter.api.assertThrows<EmailAlreadyUsed> {
            service.updateUserInfo(
                UserInfoDTO(firstName = "john", lastName = "doe", email = "second@example.com")
            )
        }
    }
}
