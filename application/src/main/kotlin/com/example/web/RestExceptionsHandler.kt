package com.example.web

import com.example.domain.accounts.AccountAlreadyExists
import com.example.domain.accounts.InvalidCredentials
import com.example.domain.accounts.InvalidPassword
import com.example.domain.accounts.InvalidToken
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionsHandler {

    @ExceptionHandler(InvalidCredentials::class)
    fun handleInvalidCredentials(): ProblemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED)

    @ExceptionHandler(AccountAlreadyExists::class)
    fun handleAccountAlreadyExists(): ProblemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT)

    @ExceptionHandler(InvalidPassword::class, InvalidToken::class)
    fun handleInvalidPassword(): ProblemDetail =
        ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY)
}
