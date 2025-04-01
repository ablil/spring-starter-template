package com.example.users

import com.example.common.AuthorityConstants
import com.example.common.JpaConfiguration
import java.time.Instant
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest

@ImportAutoConfiguration(JpaConfiguration::class)
@DataJpaTest
class UsersRepositoryTest {

    @Autowired lateinit var repository: UserRepository

    @BeforeEach
    fun setup() {
        repository.deleteAllInBatch()
    }

    @Test
    fun `should set auditing fields`() {
        val domainUser =
            repository.saveAndFlush(
                DomainUser(
                    username = "johndoe",
                    email = "johndoe@example.com",
                    password = "supersecurepassword",
                    disabled = false,
                    roles = setOf(AuthorityConstants.ADMIN),
                    firstName = null,
                    lastName = null,
                    activationKey = null,
                    resetKey = null,
                    resetDate = Instant.now(),
                )
            )
        assertThat(domainUser)
            .extracting("id", "createdAt", "createdBy", "updatedAt", "updatedBy")
            .doesNotContainNull()
    }
}
