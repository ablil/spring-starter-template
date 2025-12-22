import java.time.Instant
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("base-convention")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.springframework.dependencymanagement)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.openapigenerator)
    alias(libs.plugins.jib)
}


val isCI = System.getenv("CI") == "true"

dependencies {
    implementation(project(":domain"))
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.+")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.mockito.kotlin:mockito-kotlin:6.1.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    runtimeOnly("com.h2database:h2")
}


openApiGenerate {
    inputSpecRootDirectory = "$projectDir/src/main/resources/oas3"
    generatorName = "kotlin-spring"
    configFile = "$projectDir/src/main/resources/config/openapi.yaml"
    skipValidateSpec = true
    typeMappings = mapOf(
        "string+date-time" to Instant::class.java.name,
    )
}

tasks.compileKotlin {
    dependsOn(tasks.openApiGenerate)
}


// include generated code by openapi-generator as source code
sourceSets {
    main {
        kotlin {
            srcDir("$buildDir/generate-resources/main/src/main/kotlin")
        }
    }
}


tasks.withType<com.google.cloud.tools.jib.gradle.JibTask>().configureEach {
    notCompatibleWithConfigurationCache("because https://github.com/GoogleContainerTools/jib/issues/3132")
}

jib {
    to {
        image = project.findProperty("jib.image") as? String
            ?: throw GradleException("missing project property jib.image")

        if (project.hasProperty("jib.tags")) {
            tags = project.property("jib.tags").toString().split(",").toSet()
        }
    }

    container {
        creationTime = "USE_CURRENT_TIMESTAMP"
        ports = listOf("8080")
    }
}

tasks.build {
    if (isCI) {
        dependsOn(tasks.jib)
    }
}

tasks.bootRun {
    if (project.hasProperty("profiles")) {
        systemProperty("spring.profiles.active", project.findProperty("profiles") as String)
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

