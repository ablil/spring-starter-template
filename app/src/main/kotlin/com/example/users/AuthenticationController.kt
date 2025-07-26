package com.example.users

import jakarta.validation.constraints.NotBlank
import java.time.Instant
import org.openapitools.api.AuthenticationApi
import org.openapitools.api.PasswordApi
import org.openapitools.model.RequestPasswordResetRequest
import org.openapitools.model.ResetPasswordRequest
import org.openapitools.model.SignIn200Response
import org.openapitools.model.SignInRequest
import org.openapitools.model.SignUpRequest
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
    private val accountService: AccountService,
) : AuthenticationApi, PasswordApi {

    @Value("\${example.security.jwt.validity-in-seconds:3600}") lateinit var jwtValidity: String

    override fun signIn(signInRequest: SignInRequest): ResponseEntity<SignIn200Response> {
        val credentials =
            UsernamePasswordAuthenticationToken(signInRequest.login, signInRequest.password)
        val authentication = authenticationManagerBuilder.`object`.authenticate(credentials)
        return ResponseEntity.ok(SignIn200Response(requireNotNull(generateToken(authentication))))
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

    override fun signUp(signUpRequest: SignUpRequest): ResponseEntity<Unit> {
        accountService.registerUser(RegistrationDTO.from(signUpRequest))
        return ResponseEntity.noContent().build()
    }

    override fun requestPasswordReset(
        requestPasswordResetRequest: RequestPasswordResetRequest
    ): ResponseEntity<Unit> {
        accountService.requestPasswordReset(requestPasswordResetRequest.email)
        return ResponseEntity.noContent().build<Unit>()
    }

    override fun resetPassword(resetPasswordRequest: ResetPasswordRequest): ResponseEntity<Unit> {
        accountService.finishPasswordReset(
            resetPasswordRequest.resetKey,
            resetPasswordRequest.password,
        )
        return ResponseEntity.noContent().build()
    }
}

data class LoginDTO(@field:NotBlank val login: String, @field:NotBlank val password: String)
