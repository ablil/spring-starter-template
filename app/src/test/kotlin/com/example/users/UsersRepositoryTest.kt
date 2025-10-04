package com.example.users

import com.example.common.DEFAULT_TEST_AUDITOR
import com.example.common.JPATestConfiguration
import com.example.common.configs.AuthorityConstants
import com.example.common.entities.DomainUser
import com.example.common.repositories.UserRepository
import java.time.Instant
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig

@SpringJUnitConfig(JPATestConfiguration::class)
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
            repository.save(
                DomainUser(
                    username = "johndoe",
                    email = "johndoe@example.com",
                    password = "supersecurepassword",
                    roles = setOf(AuthorityConstants.ADMIN),
                    firstName = null,
                    lastName = null,
                    activationKey = null,
                    resetKey = null,
                    resetDate = Instant.now(),
                )
            )
        assertThat(domainUser.id).isNotEqualTo(0)
        assertThat(domainUser.updatedBy).isEqualTo(DEFAULT_TEST_AUDITOR)
        assertThat(domainUser.updatedAt).isNotEqualTo(Instant.EPOCH)
        assertThat(domainUser.createdBy).isEqualTo(DEFAULT_TEST_AUDITOR)
        assertThat(domainUser.createdAt).isNotEqualTo(Instant.EPOCH)
    }
}
