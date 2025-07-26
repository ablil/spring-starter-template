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
    implementation(libs.bundles.spring)

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockito.kotlin:mockito-kotlin:6.0.+")

    runtimeOnly("org.postgresql:postgresql")
    testRuntimeOnly("com.h2database:h2")


    implementation("ch.qos.logback:logback-classic:1.5.+")
    implementation("net.logstash.logback:logstash-logback-encoder:8.1")

    runtimeOnly("org.hibernate.orm:hibernate-micrometer")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    runtimeOnly("com.github.ben-manes.caffeine:caffeine:3.2.+")
    implementation("org.apache.commons:commons-lang3:3.17.+")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.+")
    detektPlugins("com.github.ablil:detekt-extension:v0.1.0")

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
