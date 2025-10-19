package com.example.infra

import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration

@Configuration
@EnableCaching
class CachingConfiguration {

    @Bean
    @ConditionalOnProperty("spring.type.cache", havingValue = "redis")
    fun redisCacheCustomizer(): RedisCacheManagerBuilderCustomizer =
        RedisCacheManagerBuilderCustomizer {
            // override default cache config
            it.withCacheConfiguration(
                "cache3",
                RedisCacheConfiguration.defaultCacheConfig().entryTtl(java.time.Duration.ofHours(1)),
            )
        }
}
