package com.example.common.security.ratelimit

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import jakarta.servlet.http.HttpServletRequest
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentLinkedDeque

interface RequestIdentifierResolver {
    fun resolve(request: HttpServletRequest): String
}

interface RateLimiter {
    fun resolveIdentifier(request: HttpServletRequest): String

    fun process(request: HttpServletRequest)

    fun isExceeded(request: HttpServletRequest): Boolean
}

abstract class AbstractRateLimiter(val identifierResolver: RequestIdentifierResolver) :
    RateLimiter {

    override fun resolveIdentifier(request: HttpServletRequest): String =
        identifierResolver.resolve(request)
}

@Suppress("MagicNumber")
class SlidingWindowRateLimiter(
    val slidingWindow: Long,
    val maxRate: Long,
    requestIdentifierResolver: RequestIdentifierResolver,
) : AbstractRateLimiter(requestIdentifierResolver) {
    private val rateLimiters: Cache<String, ConcurrentLinkedDeque<Long>> =
        Caffeine.newBuilder().expireAfterAccess(Duration.ofSeconds(slidingWindow)).build()

    override fun process(request: HttpServletRequest) {
        val now = Instant.now().toEpochMilli()
        val clientIp = resolveIdentifier(request)

        rateLimiters.asMap().compute(clientIp) { ip, deque ->
            val timestamps = deque ?: ConcurrentLinkedDeque<Long>()
            timestamps.addLast(now)
            timestamps.removeIf { it < now - slidingWindow * 1000 }
            timestamps
        }
    }

    override fun isExceeded(request: HttpServletRequest): Boolean =
        rateLimiters.getIfPresent(resolveIdentifier(request))?.size?.let { it > maxRate } ?: false
}
