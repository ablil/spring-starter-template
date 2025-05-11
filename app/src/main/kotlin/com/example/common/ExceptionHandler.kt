package com.example.common

import com.example.users.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
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

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArguments(ex: IllegalArgumentException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message).also {
            logger.warn(ex.stackTraceToString())
        }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.body.detail)
            .apply {
                this.properties =
                    mapOf(
                        "properties" to
                            ex.bindingResult.allErrors.associate {
                                (it as? FieldError)?.field to it.defaultMessage
                            }
                    )
            }
            .also { logger.debug(ex.stackTraceToString()) }
}
