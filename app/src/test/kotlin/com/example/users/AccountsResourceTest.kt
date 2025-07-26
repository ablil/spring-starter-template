package com.example.users

import com.example.common.IntegrationTest
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Duration
import java.time.Instant
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
@IntegrationTest
@WithMockUser
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
    fun `should create user given valid registration request`() {
        mockMvc
            .post("/api/v1/signup") {
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
            .andExpect { status { isCreated() } }

        val user =
            userRepository.findByUsernameIgnoreCase(DEFAULT_TEST_USERNAME)
                ?: fail("user not persisted")
        assertThat(user.username).isEqualTo(DEFAULT_TEST_USERNAME)
        assertThat(user.email).isEqualTo(DEFAULT_TEST_EMAIL)
        assertThat(user.password).isNotEqualTo(DEFAULT_TEST_PASSWORD)
        assertThat(passwordEncoder.matches(DEFAULT_TEST_PASSWORD, user.password))
            .`as`("user password is encrypted")
            .isTrue()
        assertThat(user.disabled).`as`("user account is disabled").isTrue()
        assertThat(user.activationKey).isNotBlank()
    }

    @Test
    fun `should NOT create user given short password or blank username`() {
        mockMvc
            .post("/api/v1/signup") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        RegistrationDTO(
                            username = "",
                            email = DEFAULT_TEST_EMAIL,
                            password = DEFAULT_TEST_PASSWORD,
                        )
                    )
            }
            .andExpect { status { isBadRequest() } }
    }

    @Test
    fun `should preventing creating a user with existing email or username`() {
        val dummyUser = userRepository.saveAndFlush(DomainUser.defaultTestUser(disabled = false))

        mockMvc
            .post("/api/v1/signup") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        RegistrationDTO(
                            username = dummyUser.username,
                            email = dummyUser.email,
                            password = "supersecurepassword",
                        )
                    )
            }
            .andExpect { status { isConflict() } }
    }

    @Test
    fun `should register user given an existing account with same email or username`() {
        userRepository.saveAndFlush(DomainUser.defaultTestUser(disabled = false))

        mockMvc
            .post("/api/v1/signup") {
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
    fun `should activate user account successfully given valid activation key`() {
        userRepository.saveAndFlush(DomainUser.defaultTestUser())

        mockMvc.get("/api/v1/accounts/activate?key=$DEFAULT_ACTIVATION_KEY").andExpect {
            status { is2xxSuccessful() }
        }

        val user =
            userRepository.findByUsernameIgnoreCase(DEFAULT_TEST_USERNAME)
                ?: fail("user not persisted")
        assertThat(user.activationKey).isNull()
        assertThat(user.disabled).isFalse()
    }

    @Test
    fun `should NOT activate user account given invalid or missing activation key`() {
        mockMvc.get("/api/v1/accounts/activate?key=doesnotexists").andExpect {
            status { is4xxClientError() }
        }
    }

    @Test
    fun `should init password reset given email of existing user`() {
        userRepository.saveAndFlush(DomainUser.defaultTestUser(disabled = false))

        mockMvc
            .post("/api/v1/resetpassword/init") {
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
    fun `should NOT reset password with with same old password`() {
        userRepository.saveAndFlush(
            DomainUser.defaultTestUser(disabled = false).apply {
                resetKey = DEFAULT_RESET_KEY
                resetDate = Instant.now()
            }
        )

        mockMvc
            .post("/api/v1/resetpassword") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        KeyAndPassword(DEFAULT_RESET_KEY, DEFAULT_TEST_PASSWORD)
                    )
            }
            .andExpect { status { isConflict() } }
    }

    @Test
    fun `should finish password reset given expired reset key`() {
        userRepository.saveAndFlush(
            DomainUser.defaultTestUser(disabled = false).apply {
                resetKey = DEFAULT_RESET_KEY
                resetDate = Instant.now().minus(Duration.ofDays(7))
            }
        )

        mockMvc
            .post("/api/v1/resetpassword") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        KeyAndPassword(DEFAULT_RESET_KEY, "newsuperpassword")
                    )
            }
            .andExpect { status { isUnprocessableEntity() } }
    }

    @Test
    fun `should finish password reset given invalid reset key`() {
        mockMvc
            .post("/api/v1/resetpassword") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        KeyAndPassword("invalidKey", DEFAULT_TEST_PASSWORD)
                    )
            }
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `should finish password reset given valid password and reset key`() {
        userRepository.saveAndFlush(
            DomainUser.defaultTestUser(disabled = false).apply {
                resetKey = DEFAULT_RESET_KEY
                resetDate = Instant.now()
            }
        )

        mockMvc
            .post("/api/v1/resetpassword") {
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
    fun `should change user password given valid password`() {
        userRepository.saveAndFlush(DomainUser.defaultTestUser(disabled = false))

        mockMvc
            .post("/api/v1/accounts/change-password") {
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
    fun `should not  change user password given same old password`() {
        userRepository.saveAndFlush(DomainUser.defaultTestUser(disabled = false))

        mockMvc
            .post("/api/v1/accounts/change-password") {
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
    fun `should not change user password given invalid current password`() {
        userRepository.saveAndFlush(DomainUser.defaultTestUser(disabled = false))

        mockMvc
            .post("/api/v1/accounts/change-password") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        ChangePasswordDTO("invalidcurrentpass", DEFAULT_TEST_PASSWORD)
                    )
            }
            .andExpect { status { isUnprocessableEntity() } }
    }

    @Test
    @WithMockUser(username = DEFAULT_TEST_USERNAME)
    fun `should update user info`() {
        userRepository.saveAndFlush(DomainUser.defaultTestUser(disabled = false))

        mockMvc
            .post("/api/v1/accounts") {
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
    fun `should prevent updating user email with an existing one`() {
        userRepository.saveAllAndFlush(
            listOf(
                DomainUser.defaultTestUser(disabled = false),
                DomainUser.defaultTestUser(disabled = false).apply {
                    username = "restedturkey"
                    email = "turkye@example.com"
                },
            )
        )

        mockMvc
            .post("/api/v1/accounts") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        UserInfoDTO(
                            firstName = "rested",
                            lastName = "turkey",
                            email = "turkye@example.com",
                        )
                    )
            }
            .andExpect { status { isConflict() } }
    }

    @Test
    @WithMockUser(username = DEFAULT_TEST_USERNAME)
    fun `should get currently authenticated user`() {
        userRepository.saveAndFlush(DomainUser.defaultTestUser(disabled = false))

        mockMvc.get("/api/v1/accounts/current").andExpectAll {
            status { isOk() }
            jsonPath("$.username") { value(DEFAULT_TEST_USERNAME) }
            jsonPath("$.password") { doesNotExist() }
            jsonPath("$.activationKey") { doesNotExist() }
            jsonPath("$.resetKey") { doesNotExist() }
        }
    }
}

fun DomainUser.Companion.defaultTestUser(disabled: Boolean = true): DomainUser =
    DomainUser(
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
