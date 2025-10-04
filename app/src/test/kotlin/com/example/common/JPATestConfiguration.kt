package com.example.common

import com.example.common.configs.JPAConfiguration
import java.util.Optional
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.data.domain.AuditorAware

const val DEFAULT_TEST_AUDITOR = "test-auditor"

@TestConfiguration
@Import(JPAConfiguration::class)
class JPATestConfiguration {

    @Bean
    fun jpaAuditor(): AuditorAware<String> =
        AuditorAware<String> { Optional.ofNullable(DEFAULT_TEST_AUDITOR) }
}
