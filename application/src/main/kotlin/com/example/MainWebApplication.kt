package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class MainWebApplication {}

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<MainWebApplication>(*args)
}
