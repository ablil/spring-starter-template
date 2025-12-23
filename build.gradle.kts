plugins {
    kotlin("jvm") version "2.2.0" apply false
    id("com.diffplug.spotless") version "8.0.0" apply false
    id("org.openapi.generator") version "7.17.+" apply false
    id("com.google.cloud.tools.jib") version "3.5.1" apply false

    id("org.jetbrains.kotlin.plugin.spring") version "2.2.0" apply false
    id("org.springframework.boot") version "3.5.0" apply false
    id("io.spring.dependency-management") version "1.1.0" apply false
    id("org.jetbrains.kotlin.plugin.jpa") version "2.2.0" apply false
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "Spring Boot Starter Template"