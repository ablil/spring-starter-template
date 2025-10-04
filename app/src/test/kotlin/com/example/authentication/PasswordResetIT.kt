package com.example.authentication

import com.example.authentication.SignInIT.Companion.DUMMY_EMAIL
import com.example.authentication.SignInIT.Companion.DUMMY_PASSWORD
import com.example.authentication.SignInIT.Companion.DUMMY_USERNAME
import com.example.authentication.services.generateRandomKey
import com.example.common.JPATestConfiguration
import com.example.common.entities.DomainUser
import com.example.common.entities.UserStatus
import com.example.common.events.PasswordChangedEvent
import com.example.common.events.PasswordResetRequested
import com.example.common.repositories.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Duration
import java.time.Instant
import kotlin.test.junit5.JUnit5Asserter.fail
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.event.ApplicationEvents
import org.springframework.test.context.event.RecordApplicationEvents
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = [JPATestConfiguration::class])
@RecordApplicationEvents
class PasswordResetIT
@Autowired
constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
) {

    @Autowired lateinit var events: ApplicationEvents

    lateinit var testUser: DomainUser

    @BeforeEach
    @WithMockUser // required to JPA auditor
    fun setup() {
        events.clear()
        userRepository.deleteAllInBatch()
        testUser =
            userRepository.save(
                DomainUser.newUser(
                        username = DUMMY_USERNAME,
                        email = DUMMY_EMAIL,
                        password = "{noop}%s".format(DUMMY_PASSWORD),
                    )
                    .also { it.activateAccount() }
            )
    }

    @Test
    fun `should request password reset for user`() {
        mockMvc
            .post("/api/v1/resetpassword/init") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(mapOf("email" to DUMMY_EMAIL))
            }
            .andExpect { status { isNoContent() } }

        val user = userRepository.findByLogin(DUMMY_EMAIL) ?: fail("user was NOT persisted")

        assertThat(user.status)
            .withFailMessage("account should be waiting for confirmation state")
            .isEqualTo(UserStatus.WAITING_FOR_CONFIRMATION)
        assertThat(user.resetKey).isNotBlank
        assertThat(user.resetDate)
            .`as`("timestamp when reset was requested")
            .isAfter(Instant.now().minus(Duration.ofMinutes(3)))

        assertThat(events.stream(PasswordResetRequested::class.java).count()).isEqualTo(1)
    }

    @Test
    fun `should ignore password reset request for none existing users`() {
        mockMvc
            .post("/api/v1/resetpassword/init") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(mapOf("email" to "randomemail@notfound.com"))
            }
            .andExpect { status { isNoContent() } }
    }

    @Test
    fun `should reset password given valid key`() {
        val user: DomainUser =
            with(testUser) {
                resetAccount(generateRandomKey())
                userRepository.save(this)
            }

        mockMvc
            .post("/api/v1/resetpassword") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "resetKey" to requireNotNull(user.resetKey),
                            "password" to "newsupersecurepassword",
                        )
                    )
            }
            .andExpect { status { isNoContent() } }

        val updatedUser = userRepository.findByLogin(DUMMY_USERNAME) ?: fail("user NOT found")

        assertThat(updatedUser.status)
            .withFailMessage { "user account is still disabled" }
            .isEqualTo(UserStatus.ACTIVE)
        assertThat(updatedUser.resetKey).isNull()
        assertThat(updatedUser.resetDate).isNull()
        assertThat(passwordEncoder.matches("newsupersecurepassword", updatedUser.password))
            .withFailMessage { "user password was NOT updated" }
            .isTrue

        assertThat(events.stream(PasswordChangedEvent::class.java).count()).isEqualTo(1)
    }

    @Test
    fun `should NOT reset password given expired key`() {
        val user: DomainUser =
            with(testUser) {
                resetAccount(generateRandomKey())
                this.resetDate = Instant.EPOCH // set reset date back in time
                userRepository.save(this)
            }

        mockMvc
            .post("/api/v1/resetpassword") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "resetKey" to requireNotNull(user.resetKey),
                            "password" to "newsupersecurepassword",
                        )
                    )
            }
            .andExpect { status { isUnprocessableEntity() } }
    }

    @Test
    fun `should NOT reset password given the same password`() {
        val user: DomainUser =
            with(testUser) {
                resetAccount(generateRandomKey())
                userRepository.save(this)
            }

        mockMvc
            .post("/api/v1/resetpassword") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "resetKey" to requireNotNull(user.resetKey),
                            "password" to DUMMY_PASSWORD,
                        )
                    )
            }
            .andExpect { status { isConflict() } }
    }
}
