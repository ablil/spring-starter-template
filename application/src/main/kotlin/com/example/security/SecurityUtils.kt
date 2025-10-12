package com.example.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.jwt.Jwt

object SecurityUtils {

    fun authenticatedUser(): String {
        return (SecurityContextHolder.getContext().authentication.principal as? Jwt)?.subject
            ?: throw IllegalStateException("expected a jwt authentication")
    }
}
