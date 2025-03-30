package com.example.users

import com.example.common.AuthorityConstants
import com.example.common.JpaConfiguration
import java.time.Instant
import kotlin.test.assertNotNull
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
    fun `auditing fields are set by JPA`() {
        val user =
            repository.saveAndFlush(
                User(
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
        assertNotNull(user.id)
        assertNotNull(user.createdAt)
        assertNotNull(user.createdBy)
        assertNotNull(user.updatedAt)
        assertNotNull(user.updatedBy)
    }
}
