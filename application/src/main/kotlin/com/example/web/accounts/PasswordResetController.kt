package com.example.web.accounts

import com.example.domain.accounts.AccountService
import com.example.domain.accounts.Token
import com.example.domain.accounts.UsernameOrEmail
import org.openapitools.api.PasswordApi
import org.openapitools.model.RequestPasswordResetRequest
import org.openapitools.model.ResetPasswordRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PasswordResetController(val accountService: AccountService) : PasswordApi {

    override fun requestPasswordReset(
        requestPasswordResetRequest: RequestPasswordResetRequest
    ): ResponseEntity<Unit> {
        accountService.requestPasswordReset(UsernameOrEmail(requestPasswordResetRequest.email))
        return ResponseEntity.noContent().build()
    }

    override fun resetPassword(resetPasswordRequest: ResetPasswordRequest): ResponseEntity<Unit> {
        accountService.resetPassword(
            Token(resetPasswordRequest.resetKey),
            resetPasswordRequest.password,
        )
        return ResponseEntity.noContent().build()
    }
}
