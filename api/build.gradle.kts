import java.time.Instant
import java.time.OffsetDateTime
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.springframework.boot)
    id("org.openapi.generator") version "7.14.+"
    id("io.spring.dependency-management")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    compileOnly("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.springframework.boot:spring-boot-starter-validation")
}
openApiGenerate {
    inputSpecRootDirectory = "$projectDir/src/main/resources/static/oas3"
    generatorName = "kotlin-spring"
    configFile = "$projectDir/src/main/resources/config/openapi.yaml"
    skipValidateSpec = true
    typeMappings = mapOf(
        "string+date-time" to Instant::class.java.name,
    )
}

sourceSets {
    main {
        kotlin {
            srcDir("$buildDir/generate-resources/main/src/main/kotlin")
        }
    }
}

tasks.withType<KotlinCompile>() {
    dependsOn(tasks.withType<GenerateTask>())
}
tasks.bootJar {
    enabled = false
}