package com.example.common.security.ratelimit

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "example.rate-limiting")
data class RateLimitingProperties(val slidingWindow: Long, val maxRate: Long)

@Configuration
@ConditionalOnBooleanProperty("example.rate-limiting.enabled")
@EnableConfigurationProperties(RateLimitingProperties::class)
class RateLimitConfig {

    @Bean
    @ConditionalOnMissingBean(RequestIdentifierResolver::class)
    fun clientIpResolver(): RequestIdentifierResolver =
        object : RequestIdentifierResolver {
            override fun resolve(request: HttpServletRequest): String = request.remoteAddr
        }

    @Bean
    @ConditionalOnMissingBean(RateLimiter::class)
    fun rateLimiter(
        rateLimitProperties: RateLimitingProperties,
        ipResolver: RequestIdentifierResolver,
    ): RateLimiter =
        SlidingWindowRateLimiter(
            rateLimitProperties.slidingWindow,
            rateLimitProperties.maxRate,
            ipResolver,
        )

    @Bean
    fun rateLimitFilter(rateLimiter: RateLimiter): RateLimitFilter = RateLimitFilter(rateLimiter)
}
