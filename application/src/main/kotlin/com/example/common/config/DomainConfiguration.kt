package com.example.common.config

import com.example.domain.accounts.AccountService
import com.example.domain.accounts.AuthenticationService
import com.example.domain.accounts.AuthenticationTokenProvider
import com.example.domain.accounts.PasswordEncoder
import com.example.domain.accounts.Token
import com.example.domain.accounts.UserAccount
import com.example.domain.accounts.UserAccountRepository
import java.time.Instant
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters

@Configuration
class DomainConfiguration {

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
    fun domainPasswordEncoder(
        delegate: org.springframework.security.crypto.password.PasswordEncoder
    ): PasswordEncoder =
        object : PasswordEncoder {
            override fun encode(rawPassword: String): String = delegate.encode(rawPassword)

            override fun match(rawPassword: String, encodedPassword: String): Boolean =
                delegate.matches(rawPassword, encodedPassword)
        }

    @Bean
    fun tokenProvider(
        jwtEncoder: JwtEncoder,
        @Value("\${spring.security.oauth2.resourceserver.jwt.validity-in-seconds}")
        jwtValidity: String,
    ): AuthenticationTokenProvider =
        object : AuthenticationTokenProvider {

            override fun apply(user: UserAccount): Token {
                val jwtHeader = JwsHeader.with(SignatureAlgorithm.RS256).build()

                val authoritiesClaim = emptyList<String>()
                val jwtPayload =
                    JwtClaimsSet.builder()
                        .subject(user.username)
                        .issuedAt(Instant.now())
                        .expiresAt(Instant.now().plusSeconds(jwtValidity.toLong()))
                        .claim("roles", authoritiesClaim)
                        .build()

                return Token(
                    requireNotNull(
                        jwtEncoder
                            .encode(JwtEncoderParameters.from(jwtHeader, jwtPayload))
                            ?.tokenValue
                    )
                )
            }
        }
}
