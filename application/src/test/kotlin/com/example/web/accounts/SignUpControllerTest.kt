package com.example.web.accounts

import com.example.domain.accounts.UserAccountRepository
import com.example.domain.accounts.UsernameOrEmail
import com.example.web.accounts.SignInControllerTest.Companion.johnDoe
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openapitools.model.SignUpRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class SignUpControllerTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Autowired lateinit var objectMapper: ObjectMapper

    @Autowired lateinit var usersRepository: UserAccountRepository

    @BeforeEach
    fun setup() {
        usersRepository.deleteAll()
    }

    @Test
    fun `should create user account successfully`() {
        mockMvc
            .post("/api/v1/signup") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        SignUpRequest(
                            username = "johndoe",
                            email = "johndoe@example.com",
                            password = "supersecurepassword",
                        )
                    )
            }
            .andExpect { status { is2xxSuccessful() } }

        assertThat(usersRepository.findByIdentifier(UsernameOrEmail("johndoe"))).isNotNull
    }

    @Test
    fun `should not create user account with same email or username`() {
        usersRepository.save(johnDoe)

        mockMvc
            .post("/api/v1/signup") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        SignUpRequest(
                            username = "johndoe",
                            email = "johndoe@example.com",
                            password = "supersecurepassword",
                        )
                    )
            }
            .andExpect { status { is4xxClientError() } }
    }
}
