package com.example.domain

import com.example.domain.accounts.AccountService
import com.example.domain.accounts.AuthenticationService
import com.example.domain.accounts.AuthenticationTokenProvider
import com.example.domain.accounts.PasswordEncoder
import com.example.domain.accounts.UserAccountRepository
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DomainAutoConfiguration {

    @Bean
    fun accountService(
        repository: UserAccountRepository,
        passwordEncoder: PasswordEncoder,
    ): AccountService = AccountService(repository, passwordEncoder)

    @Bean
    fun authenticationService(
        repository: UserAccountRepository,
        passwordEncoder: PasswordEncoder,
        tokenProvider: AuthenticationTokenProvider,
    ): AuthenticationService =
        AuthenticationService(
            repository = repository,
            passwordEncoder = passwordEncoder,
            authenticationTokenProvider = tokenProvider,
        )

    @Bean
    @ConditionalOnMissingBean
    fun plainPasswordEncoder(): PasswordEncoder =
        object : PasswordEncoder {
            override fun encode(rawPassword: String): String = rawPassword

            override fun match(rawPassword: String, encodedPassword: String): Boolean =
                rawPassword == encodedPassword
        }
}
