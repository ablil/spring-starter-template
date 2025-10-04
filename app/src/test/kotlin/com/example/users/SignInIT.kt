package com.example.users

import com.example.common.JPATestConfiguration
import com.example.common.entities.DomainUser
import com.example.common.repositories.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.stream.Stream
import org.junit.jupiter.api.BeforeEach
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
class SignInIT
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
                    username = DUMMY_USERNAME,
                    email = DUMMY_EMAIL,
                    password = "{noop}%s".format(DUMMY_PASSWORD),
                )
                .also { it.activateAccount() }
        )
    }

    @ParameterizedTest
    @MethodSource("provideValidCredentials")
    fun `should sign in given valid credentials`(login: String, password: String) {
        mockMvc
            .post("/api/v1/signin") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(mapOf("login" to login, "password" to password))
            }
            .andExpectAll {
                status { isOk() }
                jsonPath("$.token") { isNotEmpty() }
            }
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCredentials")
    fun `should NOT sign in given invalid credentials`(login: String, password: String) {
        mockMvc
            .post("/api/v1/signin") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(mapOf("login" to login, "password" to password))
            }
            .andExpect { status { isUnauthorized() } }
    }

    companion object {
        const val DUMMY_USERNAME = "fleet-crew"
        const val DUMMY_EMAIL = "fleet-crew@exaple.com"
        const val DUMMY_PASSWORD = "supersecurepassword"

        @JvmStatic
        fun provideValidCredentials(): Stream<Arguments> =
            Stream.of(
                Arguments.of(DUMMY_USERNAME, DUMMY_PASSWORD),
                Arguments.of(DUMMY_EMAIL, DUMMY_PASSWORD),
            )

        @JvmStatic
        fun provideInvalidCredentials(): Stream<Arguments> =
            Stream.of(
                Arguments.of(DUMMY_USERNAME, "invalid password"),
                Arguments.of(DUMMY_EMAIL, "invalid password"),
                Arguments.of("none existing", DUMMY_PASSWORD),
                Arguments.of("invalid username", "invalid password"),
            )
    }
}
