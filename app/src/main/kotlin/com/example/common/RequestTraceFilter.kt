package com.example.common

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.web.filter.OncePerRequestFilter

class RequestTraceFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        filterChain.doFilter(request, response)
        LOGGER.info(
            "%s %s completed %s".format(request.method, request.requestURI, response.status),
            kv("request", getRequestDetails(request)),
            kv("response", getResponseDetails(response)),
        )
    }

    companion object {
        val LOGGER = LoggerFactory.getLogger(RequestTraceFilter::class.java)
    }

    private fun getResponseDetails(response: HttpServletResponse): Map<String, Any> {
        return mapOf(
            "headers" to response.headerNames.toList().associateWith { response.getHeader(it) }
        )
    }

    private fun getRequestDetails(request: HttpServletRequest): Map<String, Any> {
        return mapOf(
            "headers" to request.headerNames.toList().associateWith { request.getHeader(it) },
            "params" to
                request.parameterNames.toList().associateWith { request.getParameterValues(it) },
        )
    }
}
