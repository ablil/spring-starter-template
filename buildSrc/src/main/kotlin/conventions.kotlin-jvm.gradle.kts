import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("org.jetbrains.kotlin.jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    systemProperty("spring.profiles.active", "test")
    useJUnitPlatform()
    testLogging {
        events("skipped", "failed")
        exceptionFormat = TestExceptionFormat.SHORT
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}