package com.example

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication class MainApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<MainApplication>(*args)
}
