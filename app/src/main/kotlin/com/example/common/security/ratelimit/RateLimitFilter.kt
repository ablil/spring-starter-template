package com.example.common.security.ratelimit

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.filter.OncePerRequestFilter

class RateLimitFilter(val rateLimiter: RateLimiter) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {

        rateLimiter.process(request)
        if (rateLimiter.isExceeded(request)) {
            dropRequest(request, response)
            return
        }
        filterChain.doFilter(request, response)
    }

    private fun dropRequest(request: HttpServletRequest, response: HttpServletResponse) {
        logger.debug("dropping request %s to %s".format(request.remoteAddr, request.requestURL))
        response.status = HttpStatus.TOO_MANY_REQUESTS.value()
    }
}
