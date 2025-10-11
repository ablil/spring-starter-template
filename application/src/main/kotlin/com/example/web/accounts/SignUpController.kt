package com.example.web.accounts

import com.example.domain.accounts.AccountService
import com.example.domain.accounts.CreateAccount
import org.openapitools.api.SignupApi
import org.openapitools.model.SignUpRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SignUpController(val accountService: AccountService) : SignupApi {

    override fun signUp(signUpRequest: SignUpRequest): ResponseEntity<Unit> {
        accountService.createAccount(
            CreateAccount(
                username = signUpRequest.username,
                email = signUpRequest.email,
                rawPassword = signUpRequest.password,
                firstName = null,
                lastName = null,
            )
        )
        return ResponseEntity.noContent().build()
    }

    override fun activateAccount(key: String): ResponseEntity<Unit> {
        accountService.activateAccount(key)
        return ResponseEntity.noContent().build()
    }
}
