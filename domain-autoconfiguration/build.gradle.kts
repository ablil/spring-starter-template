
plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.diffplug.spotless") version "8.0.0"
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.springframework.boot)
    id("io.spring.dependency-management")
}


version = rootProject.version

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(project(":infra"))
    api(project(":domain"))
    implementation("org.springframework.boot:spring-boot-autoconfigure")
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
