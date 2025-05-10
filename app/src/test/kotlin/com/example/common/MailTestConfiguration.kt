package com.example.common

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Import
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.test.context.bean.override.mockito.MockitoBean

@TestConfiguration
class MailTestConfiguration {

    @MockitoBean lateinit var javaMailSender: JavaMailSender
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(MailTestConfiguration::class)
annotation class IntegrationTest
