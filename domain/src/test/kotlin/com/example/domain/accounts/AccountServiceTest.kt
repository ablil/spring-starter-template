package com.example.domain.accounts

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.mockito.kotlin.any
import org.mockito.kotlin.check
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AccountServiceTest {

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

    val service = AccountService(repository, passwordEncoder)

    @Test
    fun `should create account successfully`() {
        val account =
            service.createAccount(
                CreateAccount(
                    username = "johndoe",
                    email = "johndoe@example.com",
                    rawPassword = "supersecurepassword",
                    firstName = "john",
                    lastName = "doe",
                )
            )

        assertThat(account.account.status).isEqualTo(AccountStatus.INACTIVE)
        assertThat(account.account.activationKey).isNotEmpty
    }

    @Test
    fun `should throw exception given an account with same username or email`() {
        whenever(repository.exists(any(), any())).thenReturn(true)
        assertThatThrownBy {
                service.createAccount(
                    CreateAccount(
                        username = "johndoe",
                        email = "johndoe@example.com",
                        rawPassword = "supersecurepassword",
                        firstName = "john",
                        lastName = "doe",
                    )
                )
            }
            .isInstanceOf(AccountAlreadyExists::class.java)
    }

    @Test
    fun `should throw exception when activating none existing token`() {
        assertThatThrownBy { service.activateAccount("doesnotexists") }
            .isInstanceOf(AccountDoesNotExists::class.java)
    }

    @Test
    fun `should throw exception when activating an active account`() {
        whenever(repository.findByActivation(any()))
            .thenReturn(johnDoe.copy(account = johnDoe.account.copy(status = AccountStatus.ACTIVE)))
        assertThatThrownBy { service.activateAccount("dummy") }
            .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `should throw exception when activating account with invalid token`() {
        whenever(repository.findByActivation(any()))
            .thenReturn(
                johnDoe.copy(account = johnDoe.account.copy(status = AccountStatus.INACTIVE))
            )
        assertThatThrownBy { service.activateAccount("unexpectedtoken") }
            .isInstanceOf(InvalidToken::class.java)
    }

    @Test
    fun `should activate account given valid token`() {
        whenever(repository.findByActivation(any()))
            .thenReturn(johnDoe.copy(account = johnDoe.account.copy(activationKey = "mytoken")))

        service.activateAccount("mytoken")

        verify(repository)
            .save(
                check {
                    assertThat(it.account.status).isEqualTo(AccountStatus.ACTIVE)
                    assertThat(it.account.activationKey)
                        .withFailMessage(
                            "activation key has NOT been removed from user account after activation"
                        )
                        .isNull()
                }
            )
    }

    @ParameterizedTest
    @CsvSource(value = arrayOf("johndoe", "johndoe@example.com"))
    fun `should request password reset given email or username`(identifier: String) {
        whenever(repository.findByIdentifier(UsernameOrEmail(identifier))).thenReturn(johnDoe)
        val token = service.requestPasswordReset(UsernameOrEmail(identifier))

        verify(repository)
            .save(
                check {
                    assertThat(it.account.status)
                        .withFailMessage(
                            "account should be inactive after requesting password reset"
                        )
                        .isEqualTo(AccountStatus.INACTIVE)
                }
            )
        assertThat(token).`as`("password reset token").isNotNull
    }

    @Test
    fun `should return null for none existing account`() {
        val token = service.requestPasswordReset(UsernameOrEmail("doesnotexists"))
        assertThat(token)
            .`as`("password reset token")
            .withFailMessage("should ignore none existing account")
            .isNull()
    }

    @Test
    fun `should throw exception when resetting user password with invalid token`() {
        assertThatThrownBy { service.resetPassword(Token("invalid"), "dummypassword") }
            .isInstanceOf(InvalidToken::class.java)
    }

    @Test
    fun `should throw exception when resetting user password with same old password`() {
        whenever(repository.findByResetPasswordToken(any()))
            .thenReturn(johnDoe.copy(account = johnDoe.account.copy(password = "mypassword")))

        assertThatThrownBy { service.resetPassword(Token("invalid"), "mypassword") }
            .isInstanceOf(InvalidPassword::class.java)
    }

    @Test
    fun `should reset user password given valid token and new password`() {
        whenever(repository.findByResetPasswordToken(any()))
            .thenReturn(
                johnDoe.copy(
                    account = johnDoe.account.copy(password = "mypassword", resetKey = "mykey")
                )
            )

        service.resetPassword(Token("mykey"), "mynewpassword")
        verify(repository)
            .save(check { assertThat(it.account.password).isEqualTo("mynewpassword") })
        // TODO: check reset key was emptied
    }

    companion object {
        val johnDoe =
            UserAccount(
                username = "johndoe",
                email = "johndoe@example.com",
                info = UserInfo(firstName = "john", lastName = "doe"),
                account =
                    AccountDetails(
                        password = "supersecurepassword",
                        status = AccountStatus.INACTIVE,
                        resetKey = null,
                        resetRequestedAt = null,
                        activationKey = "dummy",
                    ),
            )
    }
}
