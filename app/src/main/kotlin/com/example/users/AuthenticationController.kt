package com.example.users

import jakarta.validation.constraints.NotBlank
import java.time.Instant
import org.openapitools.api.AuthenticationApi
import org.openapitools.model.Authenticate200Response
import org.openapitools.model.AuthenticateRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jose.jws.MacAlgorithm
import org.springframework.security.oauth2.jwt.JwsHeader
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthenticationController(
    val jwtEncoder: JwtEncoder,
    val authenticationManagerBuilder: AuthenticationManagerBuilder,
) : AuthenticationApi {

    @Value("\${example.security.jwt.validity-in-seconds:3600}") lateinit var jwtValidity: String

    override fun authenticate(
        authenticateRequest: AuthenticateRequest
    ): ResponseEntity<Authenticate200Response> {
        val credentials =
            UsernamePasswordAuthenticationToken(
                authenticateRequest.login,
                authenticateRequest.password,
            )
        val authentication = authenticationManagerBuilder.`object`.authenticate(credentials)
        return ResponseEntity.ok(
            Authenticate200Response(requireNotNull(generateToken(authentication)))
        )
    }

    private fun generateToken(authentication: Authentication): String? {
        val jwtHeader = JwsHeader.with(MacAlgorithm.HS256).build()

        val authoritiesClaim = authentication.authorities.joinToString(" ") { it.authority }
        val jwtPayload =
            JwtClaimsSet.builder()
                .subject(authentication.name)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(jwtValidity.toLong()))
                .claim("roles", authoritiesClaim)
                .build()

        return jwtEncoder.encode(JwtEncoderParameters.from(jwtHeader, jwtPayload))?.tokenValue
    }
}

data class LoginDTO(@field:NotBlank val login: String, @field:NotBlank val password: String)
