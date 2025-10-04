package com.example.common.configs

import com.example.common.security.SecurityUtils
import java.util.Optional
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@Configuration
@EnableJpaAuditing
class JPAConfiguration {

    @Bean
    fun jpaAuditor(): AuditorAware<String> =
        AuditorAware<String> { Optional.ofNullable(SecurityUtils.currentUserLogin()) }
}
