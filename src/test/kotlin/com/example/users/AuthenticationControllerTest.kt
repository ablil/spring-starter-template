package com.example.users

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Autowired lateinit var objectMapper: ObjectMapper

    @Autowired lateinit var userRepository: UserRepository

    @BeforeEach
    fun setup() {
        userRepository.deleteAllInBatch()
    }

    @ParameterizedTest
    @CsvSource(*[DEFAULT_TEST_USERNAME, DEFAULT_TEST_EMAIL])
    fun `authenticate user given valid credentials`(login: String) {
        userRepository.saveAndFlush(DomainUser.defaultTestUser(disabled = false))

        mockMvc
            .post("/api/authenticate") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(LoginDTO(login, DEFAULT_TEST_PASSWORD))
            }
            .andExpectAll {
                status { isOk() }
                jsonPath("$.token") { isNotEmpty() }
            }
    }

    @Test
    fun `authenticate user given invalid credentials`() {
        mockMvc
            .post("/api/authenticate") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        LoginDTO("nonexistinguser", DEFAULT_TEST_PASSWORD)
                    )
            }
            .andExpect { status { is4xxClientError() } }
    }
}
