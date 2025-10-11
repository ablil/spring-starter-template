package com.example.domain.accounts

interface PasswordEncoder {

    fun encode(rawPassword: String): String

    fun match(rawPassword: String, encodedPassword: String): Boolean
}
