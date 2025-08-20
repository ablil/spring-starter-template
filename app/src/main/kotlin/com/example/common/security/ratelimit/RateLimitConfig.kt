package com.example.common.security.ratelimit

import com.example.common.security.CommonSecurityConfigurations
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnBooleanProperty
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.web.context.SecurityContextHolderFilter

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

/** add rate limiting filter to the security filter chain if found on the application context */
class RateLimitConfigurer : AbstractHttpConfigurer<CommonSecurityConfigurations, HttpSecurity>() {

    @Suppress("SwallowedException", "TooGenericExceptionCaught")
    override fun configure(builder: HttpSecurity?) {
        try {
            val rateLimitFilter =
                builder
                    ?.getSharedObject(ApplicationContext::class.java)
                    ?.getBean(RateLimitFilter::class.java)

            if (rateLimitFilter != null) {
                builder.addFilterAfter(rateLimitFilter, SecurityContextHolderFilter::class.java)
            }
        } catch (ex: Exception) {
            logger.info(
                "rate limiting was NOT configured, No filter was found on the application context"
            )
        }
    }

    companion object {
        val logger = LoggerFactory.getLogger(RateLimitConfig::class.java)
    }
}
