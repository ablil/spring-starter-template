package com.example.common.security

import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtils {

    fun currentUserLogin(): String = SecurityContextHolder.getContext().authentication.name
}
