plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.springframework.boot)
    id("io.spring.dependency-management")
    id("com.diffplug.spotless") version "7.1.+"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
    id("com.google.cloud.tools.jib") version "3.4.+"
    id("com.gorylenko.gradle-git-properties") version "2.5.+"
}


val isCI = System.getenv("CI") == "true"
version = rootProject.version

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

dependencies {
    implementation(project(":api"))
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-logging")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.apache.commons:commons-lang3:3.17.+")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.+")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly("com.h2database:h2")
    detektPlugins("com.github.ablil:detekt-extension:v0.1.0")
    // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.5.+")
    // https://mvnrepository.com/artifact/net.logstash.logback/logstash-logback-encoder
    implementation("net.logstash.logback:logstash-logback-encoder:8.1")
    runtimeOnly("com.github.ben-manes.caffeine:caffeine:3.2.+")
    runtimeOnly("org.hibernate.orm:hibernate-micrometer")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

spotless {
    kotlin {
        toggleOffOn()
        ktfmt("0.56").kotlinlangStyle()
    }
}

tasks.build {
    if (isCI) {
        dependsOn(tasks.jib)
    }
}

jib {
    to {
        val tag = project.findProperty("tag")
        image = if ( isCI ) {
            "ghcr.io/${System.getenv("GITHUB_REPOSITORY")}:$tag"
        } else {
            "${rootProject.name}:$tag"
        }
    }
    container {
        creationTime = "USE_CURRENT_TIMESTAMP"
        ports = listOf("8080")
    }
}

tasks.withType<com.google.cloud.tools.jib.gradle.JibTask>().configureEach {
    notCompatibleWithConfigurationCache("because https://github.com/GoogleContainerTools/jib/issues/3132")
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.test {
    systemProperty("spring.profiles.active", "test")
    useJUnitPlatform()
}

springBoot {
    buildInfo()
}

detekt {
    config.setFrom("src/main/resources/config/config.yml")
    buildUponDefaultConfig = true
}

gitProperties {
    dotGitDirectory.set(project.rootProject.layout.projectDirectory.dir(".git"))
}
