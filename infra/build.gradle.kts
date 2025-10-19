plugins {
    id("conventions.kotlin-jvm")
    id("conventions.spotless")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.springframework.dependencymanagement)
    alias(libs.plugins.kotlin.jpa)
}


version = rootProject.version

dependencies {
    implementation(project(":domain"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-cache")

    runtimeOnly("com.h2database:h2")
    implementation("org.slf4j:slf4j-api:2.0.17")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.1.0")
}
tasks.bootJar {
    enabled = false
}
