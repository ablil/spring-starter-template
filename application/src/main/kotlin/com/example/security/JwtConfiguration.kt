package com.example.security

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder

@Configuration
class JwtConfiguration {

    @Bean
    fun jwtEncoder(
        @Value("\${spring.security.oauth2.resourceserver.jwt.private-key-location}")
        privateKey: Resource,
        @Value("\${spring.security.oauth2.resourceserver.jwt.public-key-location}")
        publicKey: Resource,
    ): JwtEncoder {
        return NimbusJwtEncoder(
            ImmutableJWKSet(
                JWKSet(
                    RSAKey.Builder(parsePublicKey(publicKey))
                        .privateKey(parsePrivateKey(privateKey))
                        .build()
                )
            )
        )
    }

    fun parsePublicKey(resource: Resource): RSAPublicKey {
        val key =
            String(resource.inputStream.readAllBytes())
                .replace("-----\\w+ PUBLIC KEY-----".toRegex(), "")
                .replace("\\s".toRegex(), "")
        val decoded: ByteArray = java.util.Base64.getDecoder().decode(key)
        val keySpec = X509EncodedKeySpec(decoded)
        return (KeyFactory.getInstance("RSA").generatePublic(keySpec) as RSAPublicKey?)!!
    }

    fun parsePrivateKey(resource: Resource): RSAPrivateKey {
        val key =
            String(resource.inputStream.readAllBytes())
                .replace("-----\\w+ PRIVATE KEY-----".toRegex(), "")
                .replace("\\s".toRegex(), "")
        val decoded: ByteArray = java.util.Base64.getDecoder().decode(key)
        val keySpec = PKCS8EncodedKeySpec(decoded)
        return (KeyFactory.getInstance("RSA").generatePrivate(keySpec) as RSAPrivateKey?)!!
    }
}
