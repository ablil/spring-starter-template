package com.example.infra.accounts

import com.example.domain.accounts.AccountDetails
import com.example.domain.accounts.AccountStatus
import com.example.domain.accounts.UserAccount
import com.example.domain.accounts.UserAccountRepository
import com.example.domain.accounts.UserInfo
import com.example.domain.accounts.UsernameOrEmail
import com.example.infra.JPAConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ContextConfiguration

@DataJpaTest(showSql = false)
@EnableAutoConfiguration
@ContextConfiguration(classes = [UserAccountRepositoryAdapter::class, JPAConfiguration::class])
class UserAccountRepositoryAdapterTest {

    @Autowired lateinit var repository: UserAccountRepository

    @BeforeEach
    fun setup() {
        repository.deleteAll()
        repository.save(johnDoe)
    }

    @ParameterizedTest
    @CsvSource(*["johndoe", "johndoe@example.com"])
    fun `should find user by email or username`(id: String) {
        assertThat(repository.findByIdentifier(UsernameOrEmail(id))).isNotNull
    }

    companion object {
        val johnDoe =
            UserAccount(
                username = "johndoe",
                email = "johndoe@example.com",
                info = UserInfo(firstName = "john", lastName = "doe"),
                account =
                    AccountDetails(
                        password = "supersecurepassword",
                        status = AccountStatus.INACTIVE,
                        resetKey = null,
                        resetRequestedAt = null,
                        activationKey = "dummy",
                    ),
            )
    }
}
