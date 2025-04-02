package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.EnableAspectJAutoProxy

@EnableCaching
@EnableAspectJAutoProxy
@SpringBootApplication
class SpringStarterTemplateApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<SpringStarterTemplateApplication>(*args)
}
