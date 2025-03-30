package com.example.users

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
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class AccountsResourceTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Autowired lateinit var userRepository: UserRepository

    @Autowired lateinit var passwordEncoder: PasswordEncoder

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
                    """
                {
                    "username": "johndoe",
                    "email": "johndoe@example.com",
                    "password": "supersecurepassword"
                }
            """
                        .trimIndent()
            }
            .andExpect { status { isNoContent() } }

        val user = userRepository.findByUsernameIgnoreCase("johndoe") ?: fail("user not persisted")
        assertAll(
            { assertEquals("johndoe", user.username) },
            { assertEquals("johndoe@example.com", user.email) },
            { assertNotEquals("supersecurepassword", user.password) },
            { assertTrue(passwordEncoder.matches("supersecurepassword", user.password)) },
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
        userRepository.saveAndFlush(
            User(
                username = "johndoe",
                email = "johndoe@example.com",
                password = passwordEncoder.encode("supersecurepassword"),
                disabled = true,
                roles = emptySet(),
                firstName = null,
                lastName = null,
                activationKey = null,
            )
        )

        mockMvc
            .post("/api/account/register") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                {
                    "username": "johndoe",
                    "email": "johndoe@example.com",
                    "password": "supersecurepassword"
                }
            """
                        .trimIndent()
            }
            .andExpect { status { isConflict() } }
    }
}
