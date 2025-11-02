package com.example.web.accounts

import com.example.domain.accounts.AccountStatus
import com.example.domain.accounts.PasswordResetDTO
import com.example.domain.accounts.UserAccountRepository
import com.example.domain.accounts.UsernameOrEmail
import com.example.web.accounts.SignInControllerTest.Companion.johnDoe
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Duration
import java.time.Instant
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openapitools.model.RequestPasswordResetRequest
import org.openapitools.model.ResetPasswordRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class PasswordResetControllerTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Autowired lateinit var usersRepository: UserAccountRepository

    @Autowired lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        usersRepository.deleteAll()
    }

    @Test
    fun `should request password reset for existing user`() {
        usersRepository.save(
            johnDoe.copy(account = johnDoe.account.copy(status = AccountStatus.ACTIVE))
        )
        mockMvc
            .post("/api/v1/resetpassword/init") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        RequestPasswordResetRequest("johndoe@example.com")
                    )
            }
            .andExpect { status { is2xxSuccessful() } }

        val user = usersRepository.findByIdentifier(UsernameOrEmail("johndoe"))

        assertThat(user?.account?.passwordReset?.key).`as`("check reset key").isNotEmpty
        assertThat(user?.account?.passwordReset?.dueTo).isInTheFuture
    }

    @Test
    fun `should not request password reset for none existing user`() {
        mockMvc
            .post("/api/v1/resetpassword/init") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        RequestPasswordResetRequest("doesnotexists@example.com")
                    )
            }
            .andExpect { status { is2xxSuccessful() } }
    }

    @Test
    fun `should reset user password given valid reset key`() {
        usersRepository.save(
            johnDoe.copy(
                account =
                    johnDoe.account.copy(
                        passwordReset =
                            PasswordResetDTO("mykey", Instant.now().plus(Duration.ofMinutes(5)))
                    )
            )
        )
        mockMvc
            .post("/api/v1/resetpassword") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        ResetPasswordRequest(
                            resetKey = "mykey",
                            password = "mynewsupersecurepassword",
                        )
                    )
            }
            .andExpect { status { is2xxSuccessful() } }

        val user = usersRepository.findByIdentifier(UsernameOrEmail("johndoe"))

        with(SoftAssertions()) {
            this.assertThat(user?.account?.passwordReset?.key).`as` { "check reset key" }.isNull()
            this.assertThat(user?.account?.status)
                .`as`("check account status after reset")
                .isEqualTo(AccountStatus.ACTIVE)
            this.assertAll()
        }
    }

    @Test
    fun `should not reset user password given same old password`() {
        usersRepository.save(
            johnDoe.copy(
                account =
                    johnDoe.account.copy(
                        passwordReset =
                            PasswordResetDTO("mykey", Instant.now().plus(Duration.ofMinutes(5)))
                    )
            )
        )
        mockMvc
            .post("/api/v1/resetpassword") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        ResetPasswordRequest(resetKey = "mykey", password = "supersecurepassword")
                    )
            }
            .andExpect { status { is4xxClientError() } }
    }

    @Test
    fun `should not reset user password given invalid key`() {
        mockMvc
            .post("/api/v1/resetpassword") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        ResetPasswordRequest(
                            resetKey = "invalidkey",
                            password = "supersecurepassword",
                        )
                    )
            }
            .andExpect { status { is4xxClientError() } }
    }
}
