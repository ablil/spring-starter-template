plugins {
    id("conventions.kotlin-jvm")
    id("io.gatling.gradle") version "3.14.6.4"
    id("conventions.spotless")
}

group = "example-gatling"


dependencies {
    gatlingImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.20.0")
}

gatling {
}