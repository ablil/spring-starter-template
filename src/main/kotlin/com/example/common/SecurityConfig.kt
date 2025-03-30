package com.example.common

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.util.Base64
import javax.crypto.spec.SecretKeySpec
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            authorizeHttpRequests {
                authorize("/actuator/health", permitAll)
                authorize("/api/account/register", permitAll)
                authorize("/api/account/activate", permitAll)
                authorize("/api/authenticate", permitAll)
                authorize("/actuator/**", hasAuthority(AuthorityConstants.ADMIN.name))
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
}

enum class AuthorityConstants {
    ADMIN
}
