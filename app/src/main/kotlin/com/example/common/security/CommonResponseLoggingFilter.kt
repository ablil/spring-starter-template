package com.example.common.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.web.filter.OncePerRequestFilter

class CommonResponseLoggingFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        filterChain.doFilter(request, response)
        LOGGER.debug(
            "%s %s completed %s".format(request.method, request.requestURI, response.status),
            kv("response", getResponseDetails(response)),
        )
    }

    companion object {
        val LOGGER = LoggerFactory.getLogger(CommonResponseLoggingFilter::class.java)
    }

    private fun getResponseDetails(response: HttpServletResponse): Map<String, Any> {
        return mapOf(
            "headers" to response.headerNames.toList().associateWith { response.getHeader(it) }
        )
    }
}
