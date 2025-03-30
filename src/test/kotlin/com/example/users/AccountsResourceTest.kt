package com.example.users

import com.fasterxml.jackson.databind.ObjectMapper
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

private const val DEFAULT_TEST_USERNAME = "johndoe"

private const val DEFAULT_TEST_EMAIL = "johndoe@example.com"

private const val DEFAULT_TEST_PASSWORD = "supersecurepassword"

private const val DEFAULT_ACTIVATION_KEY = "activationkey"

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
    fun `register user given an existing account with same email or password`() {
        userRepository.saveAndFlush(User.defaultTestUser())

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
}

private fun User.Companion.defaultTestUser(disabled: Boolean = true): User =
    User(
        username = DEFAULT_TEST_USERNAME,
        email = DEFAULT_TEST_EMAIL,
        password = "{noop}$DEFAULT_TEST_PASSWORD",
        disabled = disabled,
        roles = emptySet(),
        firstName = "john",
        lastName = "doe",
        activationKey = DEFAULT_ACTIVATION_KEY.takeIf { disabled },
    )
