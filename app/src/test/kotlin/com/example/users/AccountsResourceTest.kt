package com.example.users

const val DEFAULT_TEST_USERNAME = "johndoe"

const val DEFAULT_TEST_EMAIL = "johndoe@example.com"

const val DEFAULT_TEST_PASSWORD = "supersecurepassword"

const val DEFAULT_ACTIVATION_KEY = "activationkey"

fun DomainUser.Companion.defaultTestUser(disabled: Boolean = true): DomainUser =
    DomainUser(
        username = DEFAULT_TEST_USERNAME,
        email = DEFAULT_TEST_EMAIL,
        password = "{noop}$DEFAULT_TEST_PASSWORD",
        disabled = disabled,
        roles = emptySet(),
        firstName = "john",
        lastName = "doe",
        activationKey = DEFAULT_ACTIVATION_KEY.takeIf { disabled },
        resetKey = null,
        resetDate = null,
    )
