package com.example.users

import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/account")
class AccountsResource(val accountService: AccountService) {

    @PostMapping("register")
    fun registerUser(@RequestBody @Valid dto: RegistrationDTO): ResponseEntity<Void> =
        accountService.registerUser(dto).let { ResponseEntity.noContent().build() }

    @GetMapping("activate")
    fun activateAccount(@RequestParam("key") key: String): ResponseEntity<Void> =
        accountService.activateAccount(key).let { ResponseEntity.noContent().build() }

    @PostMapping("password-reset/init")
    fun requestResetPassword(@RequestBody @Valid email: EmailWrapper): ResponseEntity<Void> =
        accountService.requestPasswordReset(email.email).let { ResponseEntity.noContent().build() }

    @PostMapping("password-reset/finish")
    fun finishPasswordReset(@RequestBody @Valid body: KeyAndPassword): ResponseEntity<Void> =
        accountService.finishPasswordReset(body.resetKey, body.password).let {
            ResponseEntity.noContent().build()
        }

    @PostMapping("change-password")
    fun changePassword(@RequestBody @Valid body: ChangePasswordDTO): ResponseEntity<Void> =
        accountService.changePassword(body.currentPassword, body.newPassword).let {
            ResponseEntity.noContent().build()
        }

    @GetMapping
    fun getCurrentUser(): ResponseEntity<DomainUser> =
        ResponseEntity.ok(accountService.getCurrentUser())

    @PostMapping
    fun updateUserInformation(@RequestBody @Valid body: UserInfoDTO): ResponseEntity<Void> =
        accountService.updateUserInfo(body).let { ResponseEntity.noContent().build() }
}

data class RegistrationDTO(
    @field:Size(min = 6) val username: String,
    @field:Email val email: String,
    @field:Size(min = 10) val password: String,
)

data class EmailWrapper(@field:Email val email: String)

data class KeyAndPassword(
    @field:NotBlank val resetKey: String,
    @field:Size(min = 10) val password: String,
)

data class ChangePasswordDTO(
    @field:NotBlank val currentPassword: String,
    @field:Size(min = 10) val newPassword: String,
)

data class UserInfoDTO(
    val firstName: String?,
    val lastName: String?,
    @field:Email val email: String,
)
