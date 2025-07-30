package com.example.users

import com.example.common.JPATestConfiguration
import com.example.users.SignInIT.Companion.DUMMY_EMAIL
import com.example.users.SignInIT.Companion.DUMMY_PASSWORD
import com.example.users.SignInIT.Companion.DUMMY_USERNAME
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@ContextConfiguration(classes = [JPATestConfiguration::class])
@AutoConfigureMockMvc
class AccountActivationIT
@Autowired
constructor(val mockMvc: MockMvc, val userRepository: UserRepository) {

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
            )
    }

    @Test
    fun `should activate user account given valid key`() {
        mockMvc
            .get("/api/v1/accounts/activate") {
                queryParam("key", requireNotNull(testUser.activationKey))
            }
            .andExpect { status { isNoContent() } }

        val user: DomainUser = userRepository.findByLogin(DUMMY_USERNAME) ?: fail("user NOT found")

        assertThat(user.status)
            .withFailMessage { "user account should be activated" }
            .isEqualTo(UserStatus.ACTIVE)
        assertThat(user.activationKey).withFailMessage { "activation key was not removed" }.isNull()
    }

    @Test
    fun `should NOT activate user account given none existing key`() {
        mockMvc
            .get("/api/v1/accounts/activate") { queryParam("key", "doesnotexists") }
            .andExpect { status { isNotFound() } }
    }
}
