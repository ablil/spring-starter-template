plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    // Make the Kotlin JVM plugin available for use inside buildSrc
    implementation(plugin(libs.plugins.kotlin.jvm))
    implementation("com.diffplug.spotless:com.diffplug.spotless.gradle.plugin:8.0.0")
}


// Helper function that transforms a Gradle Plugin alias from a
// Version Catalog into a valid dependency notation for buildSrc
fun DependencyHandlerScope.plugin(plugin: Provider<PluginDependency>) =
    plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }