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
}

data class RegistrationDTO(val username: String, val email: String, val password: String)
