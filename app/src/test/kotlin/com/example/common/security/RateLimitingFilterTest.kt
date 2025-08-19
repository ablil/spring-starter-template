package com.example.common.security

import com.example.common.security.ratelimit.RateLimitFilter
import com.example.common.security.ratelimit.RequestIdentifierResolver
import com.example.common.security.ratelimit.SlidingWindowRateLimiter
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import java.lang.Thread.sleep
import java.time.Duration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class RateLimitingFilterTest {

    @Test
    fun `should rate limit the requests`() {

        val maxRate = 3L
        val slidingWindow = 10L

        val rateLimitingFilter =
            RateLimitFilter(
                SlidingWindowRateLimiter(
                    slidingWindow,
                    maxRate,
                    object : RequestIdentifierResolver {
                        override fun resolve(request: HttpServletRequest): String =
                            request.remoteAddr
                    },
                )
            )
        val request = MockHttpServletRequest().apply { remoteAddr = "127.0.0.1" }
        val filterChain = mock<FilterChain>()

        // consume all allowed reqeust in current windows
        repeat(maxRate.toInt()) {
            val response = MockHttpServletResponse()
            rateLimitingFilter.doFilter(request, response, filterChain)
            assertThat(response.status)
                .withFailMessage("request should be processed successfully")
                .isEqualTo(HttpStatus.OK.value())
        }

        // check rate limiting
        repeat(5) {
            val response = MockHttpServletResponse()
            rateLimitingFilter.doFilter(request, response, filterChain)
            assertThat(response.status)
                .withFailMessage("request should be dropped")
                .isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value())
        }

        // check rate limiting after current window has expired
        val response = MockHttpServletResponse()
        sleep(Duration.ofSeconds(slidingWindow + 1))
        rateLimitingFilter.doFilter(request, response, filterChain)
        assertThat(response.status)
            .withFailMessage("request should be processed successfully")
            .isEqualTo(HttpStatus.OK.value())
    }
}
