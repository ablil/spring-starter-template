package com.example.common

import java.util.Optional
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

private const val DEFAULT_APPLICATION_AUDITOR = "system"

@Configuration
@EnableJpaAuditing
class JpaConfiguration {

    @Bean
    fun jpaAuditor(): AuditorAware<String> =
        AuditorAware<String> { Optional.ofNullable(DEFAULT_APPLICATION_AUDITOR) }
}
