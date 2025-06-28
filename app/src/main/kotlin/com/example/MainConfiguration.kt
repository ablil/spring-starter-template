package com.example

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableAsync

@Configuration @EnableCaching @EnableAspectJAutoProxy @EnableAsync class MainConfiguration
