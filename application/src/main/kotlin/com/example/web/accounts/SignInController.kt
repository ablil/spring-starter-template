package com.example.web.accounts

import com.example.domain.accounts.AuthenticationService
import com.example.domain.accounts.LoginCredentials
import com.example.domain.accounts.UsernameOrEmail
import org.openapitools.api.SigninApi
import org.openapitools.model.SignIn200Response
import org.openapitools.model.SignInRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SignInController(val authenticationService: AuthenticationService) : SigninApi {

    override fun signIn(signInRequest: SignInRequest): ResponseEntity<SignIn200Response> {
        val token =
            authenticationService.authenticate(
                LoginCredentials(
                    identifier = UsernameOrEmail(signInRequest.login),
                    rawPassword = signInRequest.password,
                )
            )
        return ResponseEntity.ok(SignIn200Response(token = token.token))
    }
}
