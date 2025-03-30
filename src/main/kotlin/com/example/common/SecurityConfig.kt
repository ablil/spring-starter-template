package com.example.common

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            authorizeHttpRequests {
                authorize("/actuator/health", permitAll)
                authorize("/api/v1/account/register", permitAll)
                authorize("/actuator/**", hasAuthority(AuthorityConstants.ADMIN.name))
                authorize(anyRequest, authenticated)
            }

            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            csrf { disable() }
            formLogin { disable() }
        }

        return http.build()
    }

    @Bean fun passwordEncoder() = PasswordEncoderFactories.createDelegatingPasswordEncoder()
}

enum class AuthorityConstants {
    ADMIN
}
