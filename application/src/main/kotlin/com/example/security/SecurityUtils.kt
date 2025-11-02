package com.example.security

import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtils {

    fun authenticatedPrincipal(): String {
        return SecurityContextHolder.getContext().authentication.principal.toString()
    }
}
