package com.example.users

import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Duration
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

const val DEFAULT_TEST_USERNAME = "johndoe"

const val DEFAULT_TEST_EMAIL = "johndoe@example.com"

const val DEFAULT_TEST_PASSWORD = "supersecurepassword"

const val DEFAULT_ACTIVATION_KEY = "activationkey"

private const val DEFAULT_RESET_KEY = "resetKey"

@SpringBootTest
@AutoConfigureMockMvc
class AccountsResourceTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Autowired lateinit var userRepository: UserRepository

    @Autowired lateinit var passwordEncoder: PasswordEncoder

    @Autowired lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        userRepository.deleteAllInBatch()
    }

    @Test
    fun `register user successfully`() {
        mockMvc
            .post("/api/account/register") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        RegistrationDTO(
                            username = DEFAULT_TEST_USERNAME,
                            email = DEFAULT_TEST_EMAIL,
                            password = DEFAULT_TEST_PASSWORD,
                        )
                    )
            }
            .andExpect { status { isNoContent() } }

        val user =
            userRepository.findByUsernameIgnoreCase(DEFAULT_TEST_USERNAME)
                ?: fail("user not persisted")
        assertAll(
            { assertEquals(DEFAULT_TEST_USERNAME, user.username) },
            { assertEquals(DEFAULT_TEST_EMAIL, user.email) },
            { assertNotEquals(DEFAULT_TEST_PASSWORD, user.password) },
            { assertTrue(passwordEncoder.matches(DEFAULT_TEST_PASSWORD, user.password)) },
            { assertThat(user.disabled).isTrue() },
            { assertThat(user.activationKey).isNotBlank() },
        )
    }

    @Test
    @Disabled
    fun `register user given short password or blank username`() {
        TODO()
    }

    @Test
    fun `register user given an existing account with same email or username`() {
        userRepository.saveAndFlush(User.defaultTestUser(disabled = false))

        mockMvc
            .post("/api/account/register") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        RegistrationDTO(
                            username = DEFAULT_TEST_USERNAME,
                            email = DEFAULT_TEST_EMAIL,
                            password = DEFAULT_TEST_PASSWORD,
                        )
                    )
            }
            .andExpect { status { isConflict() } }
    }

    @Test
    fun `activate user account successfully`() {
        userRepository.saveAndFlush(User.defaultTestUser())

        mockMvc.get("/api/account/activate?key=$DEFAULT_ACTIVATION_KEY").andExpect {
            status { is2xxSuccessful() }
        }

        val user =
            userRepository.findByUsernameIgnoreCase(DEFAULT_TEST_USERNAME)
                ?: fail("user not persisted")
        assertThat(user.activationKey).isNull()
        assertThat(user.disabled).isFalse()
    }

    @Test
    fun `activate user account given invalid or missing activation key`() {
        mockMvc.get("/api/account/activate?key=doesnotexists").andExpect {
            status { is4xxClientError() }
        }
    }

    @Test
    fun `init password reset given email of existing user`() {
        userRepository.saveAndFlush(User.defaultTestUser(disabled = false))

        mockMvc
            .post("/api/account/password-reset/init") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(EmailWrapper(DEFAULT_TEST_EMAIL))
            }
            .andExpect { status { isNoContent() } }

        val user =
            userRepository.findByEmailIgnoreCase(DEFAULT_TEST_EMAIL) ?: fail("user not persisted")
        assertThat(user.resetKey).isNotBlank()
        assertThat(user.resetKey).isNotNull()
    }

    @Test
    fun `init password reset given email of non existing user`() {
        mockMvc
            .post("/api/account/password-reset/init") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(EmailWrapper("invalidemail@example.com"))
            }
            .andExpect { status { isConflict() } }
    }

    @Test
    fun `finish password reset given same old password`() {
        userRepository.saveAndFlush(
            User.defaultTestUser(disabled = false)
                .copy(resetKey = DEFAULT_RESET_KEY, resetDate = Instant.now())
        )

        mockMvc
            .post("/api/account/password-reset/finish") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        KeyAndPassword(DEFAULT_RESET_KEY, DEFAULT_TEST_PASSWORD)
                    )
            }
            .andExpect { status { isBadRequest() } }
    }

    @Test
    fun `finish password reset given expired reset key`() {
        userRepository.saveAndFlush(
            User.defaultTestUser(disabled = false)
                .copy(
                    resetKey = DEFAULT_RESET_KEY,
                    resetDate = Instant.now().minus(Duration.ofDays(7)),
                )
        )

        mockMvc
            .post("/api/account/password-reset/finish") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        KeyAndPassword(DEFAULT_RESET_KEY, "newsuperpassword")
                    )
            }
            .andExpect { status { isConflict() } }
    }

    @Test
    fun `finish password reset given invalid reset key`() {
        mockMvc
            .post("/api/account/password-reset/finish") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        KeyAndPassword("invalidKey", DEFAULT_TEST_PASSWORD)
                    )
            }
            .andExpect { status { isConflict() } }
    }

    @Test
    fun `finish password reset successfully`() {
        userRepository.saveAndFlush(
            User.defaultTestUser(disabled = false)
                .copy(resetKey = DEFAULT_RESET_KEY, resetDate = Instant.now())
        )

        mockMvc
            .post("/api/account/password-reset/finish") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        KeyAndPassword(DEFAULT_RESET_KEY, "newsecurepassword")
                    )
            }
            .andExpect { status { isNoContent() } }

        val user =
            userRepository.findByEmailIgnoreCase(DEFAULT_TEST_EMAIL) ?: fail("user not persisted")
        assertThat(user.resetKey).isNull()
        assertThat(user.resetDate).isNull()
    }

    @Test
    @WithMockUser(username = DEFAULT_TEST_USERNAME)
    fun `change user password successfully`() {
        userRepository.saveAndFlush(User.defaultTestUser(disabled = false))

        mockMvc
            .post("/api/account/change-password") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        ChangePasswordDTO(DEFAULT_TEST_PASSWORD, "newpassword")
                    )
            }
            .andExpect { status { isNoContent() } }

        val user =
            userRepository.findByEmailIgnoreCase(DEFAULT_TEST_EMAIL) ?: fail("user not persisted")
        assertThat(passwordEncoder.matches("newpassword", user.password)).isTrue()
    }

    @Test
    @WithMockUser(username = DEFAULT_TEST_USERNAME)
    fun `change user password given same old password`() {
        userRepository.saveAndFlush(User.defaultTestUser(disabled = false))

        mockMvc
            .post("/api/account/change-password") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        ChangePasswordDTO(DEFAULT_TEST_PASSWORD, DEFAULT_TEST_PASSWORD)
                    )
            }
            .andExpect { status { isConflict() } }
    }

    @Test
    @WithMockUser(username = DEFAULT_TEST_USERNAME)
    fun `change user password given invalid current password`() {
        userRepository.saveAndFlush(User.defaultTestUser(disabled = false))

        mockMvc
            .post("/api/account/change-password") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        ChangePasswordDTO("invalidcurrentpass", DEFAULT_TEST_PASSWORD)
                    )
            }
            .andExpect { status { isConflict() } }
    }

    @Test
    @WithMockUser(username = DEFAULT_TEST_USERNAME)
    fun `update user info successfully`() {
        userRepository.saveAndFlush(User.defaultTestUser(disabled = false))

        mockMvc
            .post("/api/account") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        UserInfoDTO(
                            firstName = "rested",
                            lastName = "turkey",
                            email = "proper-sequerel@example.com",
                        )
                    )
            }
            .andExpect { status { isNoContent() } }

        val user =
            userRepository.findByUsernameIgnoreCase(DEFAULT_TEST_USERNAME)
                ?: fail("user not persisted")
        assertThat(user.firstName).isEqualTo("rested")
        assertThat(user.lastName).isEqualTo("turkey")
        assertThat(user.email).isEqualTo("proper-sequerel@example.com")
    }

    @Test
    @WithMockUser(username = DEFAULT_TEST_USERNAME)
    fun `update user info given existing email`() {
        userRepository.saveAllAndFlush(
            listOf(
                User.defaultTestUser(disabled = false),
                User.defaultTestUser(disabled = false)
                    .copy(username = "restedturkey", email = "turkye@example.com"),
            )
        )

        mockMvc
            .post("/api/account") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        UserInfoDTO(
                            firstName = "rested",
                            lastName = "turkey",
                            email = DEFAULT_TEST_EMAIL,
                        )
                    )
            }
            .andExpect { status { isConflict() } }
    }

    @Test
    @WithMockUser(username = DEFAULT_TEST_USERNAME)
    fun `get currently authenticated user`() {
        userRepository.saveAndFlush(User.defaultTestUser(disabled = false))

        mockMvc.get("/api/account").andExpectAll {
            status { isOk() }
            jsonPath("$.username") { value(DEFAULT_TEST_USERNAME) }
            jsonPath("$.password") { doesNotExist() }
            jsonPath("$.activationKey") { doesNotExist() }
            jsonPath("$.resetKey") { doesNotExist() }
        }
    }
}

fun User.Companion.defaultTestUser(disabled: Boolean = true): User =
    User(
        username = DEFAULT_TEST_USERNAME,
        email = DEFAULT_TEST_EMAIL,
        password = "{noop}$DEFAULT_TEST_PASSWORD",
        disabled = disabled,
        roles = emptySet(),
        firstName = "john",
        lastName = "doe",
        activationKey = DEFAULT_ACTIVATION_KEY.takeIf { disabled },
        resetKey = null,
        resetDate = null,
    )
