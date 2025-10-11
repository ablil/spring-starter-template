package com.example.security

import com.nimbusds.jose.jwk.source.ImmutableSecret
import com.nimbusds.jose.util.Base64
import javax.crypto.spec.SecretKeySpec
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder

@Configuration
class JwtConfiguration {

    @Bean
    fun jwtDecoder(secretKey: SecretKeySpec): JwtDecoder =
        NimbusJwtDecoder.withSecretKey(secretKey).macAlgorithm(MacAlgorithm.HS256).build()

    @Bean
    fun jwtEncoder(secretKey: SecretKeySpec): JwtEncoder =
        NimbusJwtEncoder(ImmutableSecret(secretKey))

    @Bean
    fun getSecretKey(
        @Value("\${myproperties.security.jwt.base64-secret}") base64SecretKey: String
    ): SecretKeySpec =
        Base64.from(base64SecretKey).decode().let {
            SecretKeySpec(it, 0, it.size, MacAlgorithm.HS256.name)
        }
}
