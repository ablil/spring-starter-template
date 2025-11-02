package com.example.web.accounts

import com.example.domain.accounts.AccountDetails
import com.example.domain.accounts.AccountStatus
import com.example.domain.accounts.UserAccount
import com.example.domain.accounts.UserAccountRepository
import com.example.domain.accounts.UserInfo
import com.example.web.accounts.JwtClaimMatcher.Companion.jwtHasClaim
import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jwt.SignedJWT
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.openapitools.model.SignInRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest
@AutoConfigureMockMvc
class SignInControllerTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Autowired lateinit var usersRepository: UserAccountRepository

    @Autowired lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setup() {
        usersRepository.deleteAll()
        usersRepository.save(
            johnDoe.copy(
                account =
                    johnDoe.account.copy(
                        password = "{noop}supersecurepassword",
                        status = AccountStatus.ACTIVE,
                        passwordReset = null,
                    )
            )
        )
    }

    @ParameterizedTest
    @CsvSource(*["johndoe", "johndoe@example.com"])
    fun `should authenticate user given valid credentials`(identifier: String) {
        mockMvc
            .post("/api/v1/signin") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        SignInRequest(login = identifier, password = "supersecurepassword")
                    )
            }
            .andExpectAll {
                status { is2xxSuccessful() }
                jsonPath("$.token") { isNotEmpty() }
                jsonPath("$.token", jwtHasClaim("sub", "johndoe"))
            }
    }

    @ParameterizedTest
    @CsvSource(
        *[
            "johndoe,invalidpassword",
            "johndoe@example.com,invalidpassword",
            "invaliduser,supersecurepassword",
        ]
    )
    fun `should not authenticate user given invalid credentials`(login: String, password: String) {
        mockMvc
            .post("/api/v1/signin") {
                contentType = MediaType.APPLICATION_JSON
                content =
                    objectMapper.writeValueAsString(
                        SignInRequest(login = login, password = password)
                    )
            }
            .andExpectAll { status { isUnauthorized() } }
    }

    companion object {
        val johnDoe =
            UserAccount(
                username = "johndoe",
                email = "johndoe@example.com",
                info = UserInfo(firstName = "john", lastName = "doe"),
                account =
                    AccountDetails(
                        password = "{noop}supersecurepassword",
                        status = AccountStatus.INACTIVE,
                        activationKey = "dummy",
                        passwordReset = null,
                    ),
            )
    }
}

class JwtClaimMatcher(val claim: String, val expectedValue: String) : TypeSafeMatcher<String>() {
    override fun matchesSafely(token: String?): Boolean {
        return token?.let { SignedJWT.parse(it) }?.jwtClaimsSet?.getClaim(claim)?.toString() ==
            expectedValue
    }

    override fun describeTo(description: Description?) {
        description
            ?.appendText("a jwt with claim ")
            ?.appendValue(claim)
            ?.appendText(" = ")
            ?.appendValue(expectedValue)
    }

    companion object {
        @JvmStatic
        fun jwtHasClaim(claim: String, expected: String): JwtClaimMatcher =
            JwtClaimMatcher(claim, expected)
    }
}
