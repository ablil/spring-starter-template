package com.example.persistence

import java.util.Optional
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder

@Configuration
@EnableJpaAuditing
class JPAConfiguration {

    @Bean
    fun auditor(): AuditorAware<String> =
        AuditorAware<String> {
            Optional.ofNullable(
                SecurityContextHolder.getContext().authentication.principal?.toString()
            )
        }
}
