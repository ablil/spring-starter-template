package com.example.domain.accounts

import com.example.domain.accounts.AccountServiceTest.Companion.johnDoe
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class AuthenticationServiceTest {

    val passwordEncoder =
        mock<PasswordEncoder>() {
            on { encode(any<String>()) } doAnswer { it.arguments.first() as? String }
            on { match(any<String>(), any<String>()) } doAnswer
                {
                    it.arguments.first() == it.arguments.last()
                }
        }

    val repository =
        mock<UserAccountRepository>() {
            on { save(any<UserAccount>()) } doAnswer { it.arguments.first() as UserAccount }
        }

    val tokenProvide = mock<AuthenticationTokenProvider>()
    val service = AuthenticationService(repository, passwordEncoder, tokenProvide)

    @ParameterizedTest
    @CsvSource(*["johndoe", "doesnotexists"])
    fun `should authenticate user given invalid credentials`(identifier: String) {
        whenever(repository.findActiveAccountByIdentifier((UsernameOrEmail("johndoe"))))
            .thenReturn(johnDoe.copy(account = johnDoe.account.copy(password = "validpassword")))
        assertThatThrownBy {
                service.authenticate(
                    LoginCredentials(UsernameOrEmail(identifier), "invalidpassword")
                )
            }
            .isInstanceOf(InvalidCredentials::class.java)
    }

    @Test
    fun `should authenticate user given valid credentials`() {
        whenever(tokenProvide.apply(any<UserAccount>())).thenReturn(Token("mytoken"))
        whenever(repository.findActiveAccountByIdentifier((UsernameOrEmail("johndoe"))))
            .thenReturn(johnDoe.copy(account = johnDoe.account.copy(password = "validpassword")))
        val token =
            service.authenticate(LoginCredentials(UsernameOrEmail("johndoe"), "validpassword"))
        assertThat(token).isNotNull
    }
}
