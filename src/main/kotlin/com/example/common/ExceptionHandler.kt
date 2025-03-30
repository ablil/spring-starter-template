package com.example.common

import com.example.users.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    val logger = getLogger()

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStates(ex: IllegalStateException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message).also {
            logger.warn(ex.stackTraceToString())
        }
}
