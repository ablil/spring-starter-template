package com.example.users

import com.example.common.JPATestConfiguration
import com.example.users.SignInIT.Companion.DUMMY_EMAIL
import com.example.users.SignInIT.Companion.DUMMY_PASSWORD
import com.example.users.SignInIT.Companion.DUMMY_USERNAME
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = [JPATestConfiguration::class])
class PasswordResetIT
@Autowired
constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
) {

    lateinit var testUser: DomainUser

    @BeforeEach
    @WithMockUser // required to JPA auditor
    fun setup() {
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

        assertThat(user.disabled)
            .withFailMessage("account should be disabled after requesting password reset")
            .isTrue
        assertThat(user.resetKey).isNotBlank
        assertThat(user.resetDate)
            .`as`("timestamp when reset was requested")
            .isAfter(Instant.now().minus(Duration.ofMinutes(3)))
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

        assertThat(updatedUser.disabled)
            .withFailMessage { "user account is still disabled" }
            .isFalse
        assertThat(updatedUser.resetKey).isNull()
        assertThat(updatedUser.resetDate).isNull()
        assertThat(passwordEncoder.matches("newsupersecurepassword", updatedUser.password))
            .withFailMessage { "user password was NOT updated" }
            .isTrue
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
