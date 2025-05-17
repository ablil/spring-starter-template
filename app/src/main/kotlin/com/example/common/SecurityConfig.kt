package com.example.common

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.util.Base64
import javax.crypto.spec.SecretKeySpec
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties(AdminManagementProperties::class)
class SecurityConfig {

    @Bean
    @Order
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            authorizeHttpRequests {
                authorize("/api/account/register", permitAll)
                authorize("/api/account/activate", permitAll)
                authorize("/api/authenticate", permitAll)
                authorize("/api/account/password-reset/init", permitAll)
                authorize("/api/account/password-reset/finish", permitAll)

                authorize("/swagger-ui/**", permitAll)
                authorize("/v3/api-docs/**", permitAll)
                authorize("/oas3/**", permitAll)

                authorize(anyRequest, authenticated)
            }
            oauth2ResourceServer { jwt {} }
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            csrf { disable() }
            formLogin { disable() }
        }

        return http.build()
    }

    @Bean fun passwordEncoder() = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun jwtDecoder(secretKey: SecretKeySpec): JwtDecoder {
        val decoder =
            NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build()
        return JwtDecoder { token -> decoder.decode(token) }
    }

    @Bean
    fun jwtEncoder(secretKey: SecretKeySpec): JwtEncoder =
        NimbusJwtEncoder(ImmutableSecret(secretKey))

    @Bean
    fun getSecretKey(
        @Value("\${example.security.jwt.base64-secret}") base64SecretKey: String
    ): SecretKeySpec =
        Base64.from(base64SecretKey).decode().let {
            SecretKeySpec(it, 0, it.size, MacAlgorithm.HS256.name)
        }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun adminSecurityFilterChain(
        http: HttpSecurity,
        @Qualifier("adminAuthenticationManager") authManger: AuthenticationManager,
    ): SecurityFilterChain =
        http
            .invoke {
                securityMatcher("/actuator/**")
                authorizeHttpRequests {
                    authorize("/actuator/health", permitAll)
                    authorize(anyRequest, hasAuthority(AuthorityConstants.ADMIN.name))
                }

                sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
                csrf { disable() }
                httpBasic {}
                formLogin { disable() }
                authenticationManager = authManger
            }
            .let { http.build() }

    @Bean(value = ["adminAuthenticationManager"])
    fun adminAuthenticationProvider(properties: AdminManagementProperties): AuthenticationManager {
        val userDetailsService =
            InMemoryUserDetailsManager(
                listOf(
                    User.builder()
                        .username(properties.username)
                        .password("{noop}${properties.password}")
                        .disabled(false)
                        .authorities(AuthorityConstants.ADMIN.name)
                        .accountLocked(false)
                        .accountExpired(false)
                        .build()
                )
            )
        val authenticationProvider =
            DaoAuthenticationProvider().also { it.setUserDetailsService(userDetailsService) }
        return ProviderManager(authenticationProvider)
    }
}

enum class AuthorityConstants {
    ADMIN
}
