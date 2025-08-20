package com.example.common.mail

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.thymeleaf.context.Context

@ConfigurationProperties(prefix = "example.application.urls")
data class ApplicationUrls(val activationLink: String, val resetPasswordLink: String)

@Configuration
@EnableConfigurationProperties(ApplicationUrls::class)
class MailConfig {

    @Bean
    @ConditionalOnMissingBean(MailClient::class)
    fun fallbackMailClient(): MailClient =
        object : MailClient {
            val logger = LoggerFactory.getLogger(MailConfig::class.java)

            override fun send(to: String, title: String, htmlContent: String) {
                logger.warn("mail client has NOT been configured")
            }

            override fun sendFromTemplate(
                to: String,
                title: String,
                template: String,
                context: Context?,
            ) {
                logger.warn("mail client has NOT been configured")
            }
        }
}
