package com.example.users

import com.example.common.IntegrationTest
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
@IntegrationTest
@WithMockUser
class AuthenticationControllerTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Autowired lateinit var objectMapper: ObjectMapper

    @Autowired lateinit var userRepository: UserRepository

    lateinit var dummyUser: DomainUser

    @BeforeEach
    fun setup() {
        userRepository.deleteAllInBatch()
        dummyUser = userRepository.saveAndFlush(DomainUser.defaultTestUser(disabled = false))
    }

    @Test
    fun `should authenticate user given valid credentials`() {
        // authenticate with username
        mockMvc
            .post("/api/authenticate") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        LoginDTO(dummyUser.username, DEFAULT_TEST_PASSWORD)
                    )
            }
            .andExpectAll {
                status { isOk() }
                jsonPath("$.token") { isNotEmpty() }
            }

        // authenticate with email
        mockMvc
            .post("/api/authenticate") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        LoginDTO(dummyUser.username, DEFAULT_TEST_PASSWORD)
                    )
            }
            .andExpectAll {
                status { isOk() }
                jsonPath("$.token") { isNotEmpty() }
            }
    }

    @Test
    fun `should return 401 for non-existent user`() {
        mockMvc
            .post("/api/authenticate") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(LoginDTO("nonexisting", "dummypassword"))
            }
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `should return 401 for valid user given wrong password`() {
        mockMvc
            .post("/api/authenticate") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(LoginDTO(dummyUser.username, "invalidpassword"))
            }
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `should return 401 for disabled user`() {
        val disabledUser = userRepository.saveAndFlush(dummyUser.apply { disabled = true })
        mockMvc
            .post("/api/authenticate") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        LoginDTO(disabledUser.username, DEFAULT_TEST_PASSWORD)
                    )
            }
            .andExpect { status { isUnauthorized() } }
    }

    @Test
    fun `should return 400 given blank credentials`() {
        mockMvc
            .post("/api/authenticate") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    """
                    {"login": "", "password": " " }
                """
                        .trimIndent()
            }
            .andExpect { status { isBadRequest() } }
    }
}
