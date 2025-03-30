package com.example.users

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/account")
class AccountsResource(val userService: UserService) {

    @PostMapping("register")
    fun registerUser(@RequestBody dto: RegistrationDTO): ResponseEntity<Void> =
        userService.registerUser(dto).let { ResponseEntity.noContent().build() }

    @GetMapping("activate")
    fun activateAccount(@RequestParam("key") key: String): ResponseEntity<Void> =
        userService.activateAccount(key).let { ResponseEntity.noContent().build() }

    @PostMapping("password-reset/init")
    fun requestResetPassword(@RequestBody email: EmailWrapper): ResponseEntity<Void> =
        userService.requestPasswordReset(email.email).let { ResponseEntity.noContent().build() }

    @PostMapping("password-reset/finish")
    fun finishPasswordReset(@RequestBody body: KeyAndPassword): ResponseEntity<Void> =
        userService.finishPasswordReset(body.resetKey, body.password).let {
            ResponseEntity.noContent().build()
        }

    @PostMapping("change-password")
    fun changePassword(@RequestBody body: ChangePasswordDTO): ResponseEntity<Void> =
        userService.changePassword(body.currentPassword, body.newPassword).let {
            ResponseEntity.noContent().build()
        }
}

data class RegistrationDTO(val username: String, val email: String, val password: String)

data class EmailWrapper(val email: String)

data class KeyAndPassword(val resetKey: String, val password: String)

data class ChangePasswordDTO(val currentPassword: String, val newPassword: String)
