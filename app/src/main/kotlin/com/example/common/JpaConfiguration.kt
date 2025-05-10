package com.example.common

import java.util.Optional
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing
class JpaConfiguration {

    @Bean
    @Profile("!test")
    fun jpaAuditor(): AuditorAware<String> =
        AuditorAware<String> { Optional.ofNullable(SecurityUtils.currentUserLogin()) }

    @Bean
    @Profile("test")
    fun testJpaAuditor(): AuditorAware<String> =
        AuditorAware<String> { Optional.ofNullable("testuser") }
}
