plugins {
    id("conventions.kotlin-jvm")
    id("conventions.spotless")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.springframework.dependencymanagement)
}


version = rootProject.version


dependencies {
    implementation(project(":infra"))
    api(project(":domain"))
    implementation("org.springframework.boot:spring-boot-autoconfigure")
}

tasks.bootJar {
    enabled = false
}