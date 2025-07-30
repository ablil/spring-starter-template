package com.example.users

import com.example.common.JPATestConfiguration
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.stream.Stream
import kotlin.test.junit5.JUnit5Asserter
import kotlin.text.format
import org.assertj.core.api.Assertions
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
import org.springframework.test.web.servlet.post

@SpringBootTest
@ContextConfiguration(classes = [JPATestConfiguration::class])
@AutoConfigureMockMvc
class SignUpIT
@Autowired
constructor(
    val mockMvc: MockMvc,
    val objectMapper: ObjectMapper,
    val userRepository: UserRepository,
) {

    @BeforeEach
    @WithMockUser // required to JPA auditor
    fun setup() {
        userRepository.deleteAllInBatch()
        userRepository.save(
            DomainUser.newUser(
                    username = SignInIT.Companion.DUMMY_USERNAME,
                    email = SignInIT.Companion.DUMMY_EMAIL,
                    password = "{noop}%s".format(SignInIT.Companion.DUMMY_PASSWORD),
                )
                .also { it.activateAccount() }
        )
    }

    @Test
    fun `should sign up user given valid request`() {
        mockMvc
            .post("/api/v1/signup") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "username" to "newusername",
                            "email" to "newemail@example.com",
                            "password" to "supersecurepassword",
                        )
                    )
            }
            .andExpect { status { isCreated() } }

        val user =
            userRepository.findByLogin("newusername") ?: JUnit5Asserter.fail("user was NOT created")

        Assertions.assertThat(user.email).isEqualTo("newemail@example.com")
        Assertions.assertThat(user.password)
            .`as`("encoded password")
            .isNotEqualTo("supersecurepassword")
        Assertions.assertThat(user.status).isEqualTo(UserStatus.WAITING_FOR_CONFIRMATION)
        Assertions.assertThat(user.roles).isEmpty()
        Assertions.assertThat(user.activationKey)
            .withFailMessage("activation key was not generated")
            .isNotBlank
    }

    @ParameterizedTest
    @MethodSource("provideSignUpLogin")
    fun `should NOT sign up user given an existing user with same email or password`(
        username: String,
        email: String,
    ) {
        mockMvc
            .post("/api/v1/signup") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        mapOf(
                            "username" to username,
                            "email" to email,
                            "password" to "supersecurepassword",
                        )
                    )
            }
            .andExpect { status { isConflict() } }
    }

    companion object {
        @JvmStatic
        fun provideSignUpLogin() =
            Stream.of(
                Arguments.of(SignInIT.Companion.DUMMY_USERNAME, "newemail@example.com"),
                Arguments.of("newusername", SignInIT.Companion.DUMMY_EMAIL),
            )
    }
}
