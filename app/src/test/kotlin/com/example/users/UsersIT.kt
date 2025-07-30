package com.example.users

import com.example.common.JPATestConfiguration
import com.example.users.SignInIT.Companion.DUMMY_EMAIL
import com.example.users.SignInIT.Companion.DUMMY_PASSWORD
import com.example.users.SignInIT.Companion.DUMMY_USERNAME
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.stream.Stream
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put

@SpringBootTest
@ContextConfiguration(classes = [JPATestConfiguration::class])
@AutoConfigureMockMvc
@WithMockUser(authorities = ["ADMIN"])
class UsersIT
@Autowired
constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val userRepository: UserRepository,
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
    fun `should return user`() {
        mockMvc.get("/api/v1/users/$DUMMY_USERNAME").andExpectAll {
            status { isOk() }
            jsonPath("$.username") { equals(testUser.username) }
            jsonPath("$.email") { equals(testUser.email) }
            jsonPath("$.disabled") { value(!(testUser.isActive())) }
            jsonPath("$.roles") { value(testUser.roles.map { it.name }) }
        }
    }

    @Test
    fun `should create user`() {
        mockMvc
            .post("/api/v1/users") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "username" to "newusername",
                            "email" to "newemail@example.com",
                            "firstName" to "new first name",
                            "lastName" to "new last name",
                            "roles" to arrayOf("ADMIN"),
                        )
                    )
            }
            .andExpect { status { isCreated() } }
    }

    @ParameterizedTest
    @MethodSource("provideDuplicateLogins")
    fun `should prevent prevent creating a user with used username or email`(
        username: String,
        email: String,
    ) {
        mockMvc
            .post("/api/v1/users") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "username" to username,
                            "email" to email,
                            "firstName" to "new first name",
                            "lastName" to "new last time",
                            "roles" to emptyArray<String>(),
                        )
                    )
            }
            .andExpect { status { isConflict() } }
    }

    @Test
    fun `should updated user`() {
        mockMvc
            .put("/api/v1/users/$DUMMY_USERNAME") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "username" to "newusername",
                            "email" to "newemail@example.com",
                            "firstName" to "new first name",
                            "lastName" to "new last name",
                            "roles" to arrayOf("ADMIN"),
                        )
                    )
            }
            .andExpect { status { isOk() } }
    }

    @ParameterizedTest
    @MethodSource("provideDuplicateLogins")
    fun `should prevent prevent updating a user with used username or email`(
        username: String,
        email: String,
    ) {
        if (!(userRepository.existsByUsernameIgnoreCase(username))) {
            userRepository.save(
                DomainUser.newUser(
                    username = username,
                    email = "ranomd@exmaple.com",
                    password = "{noop}password",
                )
            )
        }

        if (!(userRepository.existsByEmailIgnoreCase(email))) {
            userRepository.save(
                DomainUser.newUser(
                    username = "randomeoaisdf",
                    email = email,
                    password = "{noop}password",
                )
            )
        }
        mockMvc
            .put("/api/v1/users/$DUMMY_USERNAME") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "username" to username,
                            "email" to email,
                            "firstName" to "new first name",
                            "lastName" to "new last time",
                            "roles" to emptyArray<String>(),
                        )
                    )
            }
            .andExpect { status { isConflict() } }
    }

    @Test
    fun `should NOT update a user that does NOT exists`() {
        mockMvc
            .put("/api/v1/users/doesnotexists") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "username" to "random",
                            "email" to "random@example.com",
                            "firstName" to "new first name",
                            "lastName" to "new last time",
                            "roles" to emptyArray<String>(),
                        )
                    )
            }
            .andExpect { status { isNotFound() } }
    }

    @Test
    fun `should delete a user `() {
        mockMvc.delete("/api/v1/users/$DUMMY_USERNAME").andExpect { status { isNoContent() } }
    }

    @Test
    fun `should NOT delete a user that does NOT exists`() {
        mockMvc.delete("/api/v1/users/doesnotexists").andExpect { status { isNotFound() } }
    }

    companion object {
        @JvmStatic
        fun provideDuplicateLogins() =
            Stream.of(
                Arguments.of(DUMMY_USERNAME, "newemail@example.com"),
                Arguments.of("newusername", DUMMY_EMAIL),
            )
    }
}
