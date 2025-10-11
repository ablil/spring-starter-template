import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.diffplug.spotless") version "8.0.0"
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.kotlin.jpa)
    id("io.spring.dependency-management")
}


version = rootProject.version

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(project(":domain"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("com.h2database:h2")
    implementation("org.slf4j:slf4j-api:2.0.17")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.1.0")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

spotless {
    kotlin {
        toggleOffOn()
        ktfmt("0.58").kotlinlangStyle()
    }
}



tasks.test {
    useJUnitPlatform()
    testLogging {
        events("skipped", "failed")
        exceptionFormat = TestExceptionFormat.SHORT
    }
}
