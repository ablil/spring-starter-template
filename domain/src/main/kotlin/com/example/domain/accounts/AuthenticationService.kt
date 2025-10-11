package com.example.domain.accounts

class AuthenticationService(
    val repository: UserAccountRepository,
    val passwordEncoder: PasswordEncoder,
    val authenticationTokenProvider: AuthenticationTokenProvider,
) {

    fun authenticate(credentials: LoginCredentials): Token {
        val account =
            repository.findActiveAccountByIdentifier(credentials.identifier)?.takeIf {
                passwordEncoder.match(credentials.rawPassword, it.account.password)
            } ?: throw InvalidCredentials("invalid credentials")

        return authenticationTokenProvider.apply(account)
    }
}

data class InvalidCredentials(val msg: String) : RuntimeException(msg)

data class LoginCredentials(val identifier: UsernameOrEmail, val rawPassword: String)
