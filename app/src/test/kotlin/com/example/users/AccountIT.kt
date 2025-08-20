package com.example.users

import com.example.common.JPATestConfiguration
import com.example.common.events.PasswordChangedEvent
import com.example.users.SignInIT.Companion.DUMMY_EMAIL
import com.example.users.SignInIT.Companion.DUMMY_PASSWORD
import com.example.users.SignInIT.Companion.DUMMY_USERNAME
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.event.ApplicationEvents
import org.springframework.test.context.event.RecordApplicationEvents
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@SpringBootTest
@ContextConfiguration(classes = [JPATestConfiguration::class])
@AutoConfigureMockMvc
@RecordApplicationEvents
class AccountIT
@Autowired
constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val userRepository: UserRepository,
) {

    @Autowired lateinit var events: ApplicationEvents

    lateinit var testUser: DomainUser

    @BeforeEach
    @WithMockUser
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
    @WithMockUser(username = DUMMY_USERNAME, roles = ["FOO", "BAR"])
    fun `should return currently authenticated account`() {
        mockMvc.get("/api/v1/accounts/current").andExpectAll {
            status { isOk() }
            jsonPath("$.id") { isNotEmpty() }
            jsonPath("$.username") { equals(DUMMY_USERNAME) }
            jsonPath("$.email") { equals(DUMMY_EMAIL) }
        }
    }

    @Test
    @WithMockUser(DUMMY_USERNAME)
    fun `should update info for the authenticated user`() {
        mockMvc
            .put("/api/v1/accounts/$DUMMY_USERNAME") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "firstName" to "new first name",
                            "lastName" to "new last name",
                            "email" to "newemail@example.com",
                        )
                    )
            }
            .andExpect { status { isNoContent() } }

        val updatedUser = userRepository.findByLogin(DUMMY_USERNAME) ?: fail { "user NOT found" }
        assertThat(updatedUser.firstName).isEqualTo("new first name")
        assertThat(updatedUser.lastName).isEqualTo("new last name")
        assertThat(updatedUser.email).isEqualTo("newemail@example.com")
    }

    @Test
    @WithMockUser("anotheruser")
    fun `should prevent updating info for another users`() {
        mockMvc
            .put("/api/v1/accounts/$DUMMY_USERNAME") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "firstName" to "new first name",
                            "lastName" to "new last name",
                            "email" to "newemail@example.com",
                        )
                    )
            }
            .andExpect { status { isForbidden() } }
    }

    @Test
    @WithMockUser(DUMMY_USERNAME)
    fun `should prevent setting email to already existing user`() {
        val anotherUser =
            userRepository.save(
                DomainUser.newUser("newusername", "newemail@example.com", "supersecurepassword")
            )

        mockMvc
            .put("/api/v1/accounts/$DUMMY_USERNAME") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "firstName" to "new first name",
                            "lastName" to "new last name",
                            "email" to anotherUser.email,
                        )
                    )
            }
            .andExpect { status { isConflict() } }
    }

    @Test
    @WithMockUser(DUMMY_USERNAME)
    fun `should change user password`() {
        mockMvc
            .post("/api/v1/accounts/$DUMMY_USERNAME/change-password") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "currentPassword" to DUMMY_PASSWORD,
                            "newPassword" to "newsupersecurepassword",
                        )
                    )
            }
            .andExpect { status { isNoContent() } }

        assertThat(events.stream(PasswordChangedEvent::class.java).count()).isEqualTo(1)
    }

    @Test
    @WithMockUser("anotheruser")
    fun `should prevent updating password of another user`() {
        mockMvc
            .post("/api/v1/accounts/$DUMMY_USERNAME/change-password") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "currentPassword" to DUMMY_PASSWORD,
                            "newPassword" to "newsupersecurepassword",
                        )
                    )
            }
            .andExpect { status { isForbidden() } }
    }

    @Test
    @WithMockUser(DUMMY_USERNAME)
    fun `should NOT update password with old one`() {
        mockMvc
            .post("/api/v1/accounts/$DUMMY_USERNAME/change-password") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf("currentPassword" to DUMMY_PASSWORD, "newPassword" to DUMMY_PASSWORD)
                    )
            }
            .andExpect { status { isUnprocessableEntity() } }
    }

    @Test
    @WithMockUser(DUMMY_USERNAME)
    fun `should NOT update password given incorrect current one`() {
        mockMvc
            .post("/api/v1/accounts/$DUMMY_USERNAME/change-password") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "currentPassword" to "incorrectpassword",
                            "newPassword" to "newsupersecurepassword",
                        )
                    )
            }
            .andExpect { status { isUnprocessableEntity() } }
    }
}
