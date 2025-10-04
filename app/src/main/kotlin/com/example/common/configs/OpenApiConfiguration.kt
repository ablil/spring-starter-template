package com.example.common.configs

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.info.Info
import org.springframework.context.annotation.Configuration

@Configuration
@OpenAPIDefinition(
    info =
        Info(
            title = "spring-starter-template",
            version = "0.0.1",
            description = "spring-starter-template",
        )
)
class OpenApiConfiguration
