import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    id("kotlin-convention")
    id("formatting-linting-convention")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.1"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("org.apache.commons:commons-lang3:3.20.0")

    implementation("org.slf4j:slf4j-api:2.0.17")
    testImplementation("org.slf4j:slf4j-nop:2.0.17")

    testImplementation("org.mockito.kotlin:mockito-kotlin:6.1.0")
    testImplementation("org.assertj:assertj-core:3.27.6")
}



tasks.test {
    systemProperty("spring.profiles.active", "test")
    useJUnitPlatform()
    testLogging {
        events("skipped", "failed")
        exceptionFormat = TestExceptionFormat.SHORT
    }
}

