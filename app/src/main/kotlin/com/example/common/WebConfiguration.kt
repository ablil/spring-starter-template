package com.example.common

import java.time.Duration
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.http.CacheControl
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.mvc.WebContentInterceptor

@Configuration
class WebConfiguration : WebMvcConfigurer {

    @Value("\${spring.web.resources.cache.cachecontrol.max-age:300}")
    lateinit var cacheControlMaxAge: String

    override fun addInterceptors(registry: InterceptorRegistry) {
        val cacheControlInterceptor =
            WebContentInterceptor().apply {
                addCacheMapping(
                    CacheControl.maxAge(Duration.ofSeconds(cacheControlMaxAge.toLong()))
                        .cachePublic(),
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                )
            }

        registry.addInterceptor(cacheControlInterceptor)
    }
}
