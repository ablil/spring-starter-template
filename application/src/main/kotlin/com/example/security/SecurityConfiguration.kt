package com.example.security

import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfiguration {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {

        http.invoke {
            httpBasic { disable() }
            formLogin { disable() }
            cors { disable() }
            csrf { disable() }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            oauth2ResourceServer { jwt {} }

            authorizeHttpRequests {
                // public endpoints
                authorize("/api/v1/signup", permitAll)
                authorize("/api/v1/accounts/activate", permitAll)
                authorize("/api/v1/signin", permitAll)
                authorize("/api/v1/resetpassword/init", permitAll)
                authorize("/api/v1/resetpassword", permitAll)

                // private endpoints
                authorize("/", authenticated)

                // deny everything else
                authorize(anyRequest, denyAll)
            }
        }

        return http.build()
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun actuatorSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            securityMatcher("/actuator/**")
            authorizeHttpRequests {
                authorize("/actuator/health", permitAll)
                authorize("/actuator/info", permitAll)
                authorize(anyRequest, denyAll)
            }
        }
        return http.build()
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun swaggerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            securityMatcher("/v3/api-docs/**", "/swagger-ui/**")
            authorizeHttpRequests { authorize(anyRequest, permitAll) }
        }
        return http.build()
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun h2SecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            securityMatcher(PathRequest.toH2Console())
            csrf { disable() }
            headers { frameOptions { sameOrigin = true } }
        }
        return http.build()
    }

    @Bean
    fun securityPasswordEncoder(): PasswordEncoder =
        PasswordEncoderFactories.createDelegatingPasswordEncoder()
}
