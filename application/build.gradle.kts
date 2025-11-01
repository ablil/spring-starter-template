import java.time.Instant

plugins {
    id("conventions.kotlin-jvm")
    id("conventions.spotless")
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.springframework.dependencymanagement)
    alias(libs.plugins.kotlin.jpa)
    id("org.openapi.generator") version "7.17.+"
    id("com.google.cloud.tools.jib") version "3.4.5"
}


version = rootProject.version
val isCI = System.getenv("CI") == "true"

dependencies {
    implementation(project(":domain-autoconfiguration"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.+")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testRuntimeOnly("com.h2database:h2")
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

tasks.spotlessKotlin {
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
        image = project.findProperty("jib.image") as? String ?: throw GradleException("missing project property jib.image")
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