package com.example.persistence

import java.util.Optional
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.repository.init.Jackson2RepositoryPopulatorFactoryBean
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

    @Profile("!prod")
    @Bean
    fun testUsersPopulator(): Jackson2RepositoryPopulatorFactoryBean =
        with(Jackson2RepositoryPopulatorFactoryBean()) {
            this.setResources(arrayOf(ClassPathResource("testdata/users.json")))
            this
        }
}
