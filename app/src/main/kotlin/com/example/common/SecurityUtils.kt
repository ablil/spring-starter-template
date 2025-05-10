package com.example.common

import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtils {

    fun currentUserLogin(): String = SecurityContextHolder.getContext().authentication.name
}
