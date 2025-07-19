package com.example.common

import com.example.users.getLogger
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
open class ApplicationException(msg: String? = null) : RuntimeException(msg)

@RestControllerAdvice
class ExceptionHandler {

    val logger = getLogger()

    @ExceptionHandler(IllegalStateException::class)
    fun handleIllegalStates(ex: IllegalStateException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.message).also {
            logger.error("encountered illegal state {}", ex.message, ex)
        }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArguments(ex: IllegalArgumentException) =
        ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.message).also {
            logger.info("encountered illegal argument {}", ex.message, ex)
        }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ProblemDetail {
        val fieldErrors = ex.bindingResult.fieldErrors.associate { it.field to it.defaultMessage }
        val objectErrors = ex.bindingResult.globalErrors.map { it.defaultMessage }
        val problemDetails =
            ProblemDetail.forStatusAndDetail(
                    HttpStatus.BAD_REQUEST,
                    "Validation failed for request body",
                )
                .apply {
                    this.title = "Validation error"
                    this.properties = mutableMapOf()
                    if (fieldErrors.isNotEmpty()) {
                        this.properties?.set("fieldErrors", objectErrors)
                    }
                    if (objectErrors.isNotEmpty()) {
                        this.properties?.set("objectErrors", objectErrors)
                    }
                }
        logger.info("validation exception {}", problemDetails, ex)
        return problemDetails
    }
}
