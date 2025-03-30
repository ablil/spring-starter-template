package com.example.users

import java.time.Instant
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
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class AuthenticationController(
    val jwtEncoder: JwtEncoder,
    val authenticationManagerBuilder: AuthenticationManagerBuilder,
) {

    @Value("\${example.security.jwt.validity:0}") lateinit var jwtValidity: String

    @PostMapping("authenticate")
    fun authenticate(@RequestBody login: LoginDTO): ResponseEntity<Token> {
        val credentials = UsernamePasswordAuthenticationToken(login.login, login.password)
        val authentication = authenticationManagerBuilder.`object`.authenticate(credentials)
        return ResponseEntity.ok(Token(requireNotNull(generateToken(authentication))))
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

data class LoginDTO(val login: String, val password: String)

data class Token(val token: String)
