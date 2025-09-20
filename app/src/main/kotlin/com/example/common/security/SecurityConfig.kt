package com.example.common.security

import com.example.common.security.ratelimit.RateLimitConfig
import com.example.common.security.ratelimit.RateLimitConfigurer
import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.util.Base64
import javax.crypto.spec.SecretKeySpec
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
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
import org.springframework.web.filter.CommonsRequestLoggingFilter

@ConfigurationProperties(prefix = "example.technical-user")
data class TechnicalUserProperties(val username: String, val password: String)

@Configuration
@EnableMethodSecurity
@Import(RateLimitConfig::class)
@EnableConfigurationProperties(TechnicalUserProperties::class)
class SecurityConfig {

    @Bean
    fun requestLogger() =
        CommonsRequestLoggingFilter().apply {
            this.setIncludePayload(true)
            this.setIncludeHeaders(true)
            this.setIncludeQueryString(true)
        }

    @Bean
    fun apiFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            securityMatcher("/api/**")
            with(CommonSecurityConfigurations())
            with(RateLimitConfigurer())
            authorizeHttpRequests {
                authorize("/api/v1/signup", permitAll)
                authorize("/api/v1/accounts/activate", permitAll)
                authorize("/api/v1/signin", permitAll)
                authorize("/api/v1/resetpassword/init", permitAll)
                authorize("/api/v1/resetpassword", permitAll)

                authorize("/api/v1/users/**", hasAuthority(AuthorityConstants.ADMIN.name))
                authorize(anyRequest, authenticated)
            }

            oauth2ResourceServer { jwt {} }
        }
        return http.build()
    }

    @Bean
    @ConditionalOnBooleanProperty("example.technical-user.enabled")
    fun actuatorFilterChain(
        http: HttpSecurity,
        technicalUserProperties: TechnicalUserProperties,
    ): SecurityFilterChain {
        http.invoke {
            securityMatcher("/actuator/**")
            with(CommonSecurityConfigurations())
            with(TechnicalUserBasicAuthConfigurer(technicalUserProperties))
            authorizeHttpRequests {
                authorize("/actuator/health", permitAll)
                authorize("/actuator/info", permitAll)

                authorize(anyRequest, hasAuthority(AuthorityConstants.ADMIN.name))
            }
        }
        return http.build()
    }

    @Bean
    fun swaggerFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            securityMatcher("/swagger-ui/**", "/v3/api-docs/**", "/oas3/**")
            with(CommonSecurityConfigurations())
            authorizeHttpRequests { authorize(anyRequest, permitAll) }
        }
        return http.build()
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    fun fallbackSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            authorizeHttpRequests {
                authorize("/actuator/health", permitAll)
                authorize("/error", permitAll)
                authorize(anyRequest, denyAll)
            }
        }
        return http.build()
    }

    @Bean fun passwordEncoder() = PasswordEncoderFactories.createDelegatingPasswordEncoder()

    @Bean
    fun jwtDecoder(secretKey: SecretKeySpec): JwtDecoder =
        NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build()

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
}

enum class AuthorityConstants {
    ADMIN
}

class CommonSecurityConfigurations :
    AbstractHttpConfigurer<CommonSecurityConfigurations, HttpSecurity>() {

    override fun init(builder: HttpSecurity?) {
        builder
            ?.csrf()
            ?.disable()
            ?.formLogin()
            ?.disable()
            ?.cors()
            ?.disable()
            ?.sessionManagement()
            ?.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    }
}

class TechnicalUserBasicAuthConfigurer(val technicalUserProperties: TechnicalUserProperties) :
    AbstractHttpConfigurer<TechnicalUserBasicAuthConfigurer, HttpSecurity>() {
    override fun init(builder: HttpSecurity?) {
        builder?.httpBasic {}
    }

    override fun configure(builder: HttpSecurity?) {
        builder?.authenticationManager(
            ProviderManager(
                DaoAuthenticationProvider(
                    InMemoryUserDetailsManager(
                        listOf(
                            User.builder()
                                .username(technicalUserProperties.username)
                                .password("{noop}%s".format(technicalUserProperties.password))
                                .authorities(AuthorityConstants.ADMIN.name)
                                .disabled(false)
                                .accountLocked(false)
                                .accountExpired(false)
                                .build()
                        )
                    )
                )
            )
        )
    }
}
