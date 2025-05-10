package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableAsync

@EnableCaching
@EnableAspectJAutoProxy
@EnableAsync
@SpringBootApplication
class SpringStarterTemplateApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<SpringStarterTemplateApplication>(*args)
}
