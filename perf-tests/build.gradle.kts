plugins {
    id("conventions.kotlin-jvm")
    id("io.gatling.gradle") version "3.14.9"
    id("com.google.cloud.tools.jib") version "3.5.1"
}
tasks.register("gatlingJar", Jar::class) {
    group = "build"
    archiveBaseName.set("gatling-performance-analysis")
    dependsOn("gatlingClasses", "processResources")

    manifest {
        attributes["Implementation-Title"] = "Gradle Jar File Example"
        attributes["Implementation-Version"] = archiveVersion
        attributes["Main-Class"] = "io.gatling.app.Gatling"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.gatling.get().output)
    from(configurations.kotlinCompilerPluginClasspathGatling.get().map { if (it.isDirectory) it else zipTree(it) })
    from(configurations.gatling.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/MANIFEST.MF")
        exclude("META-INF/*.SF")
        exclude("META-INF/*.DSA")
        exclude("META-INF/*.RSA")
    }
    with(tasks.jar.get() as CopySpec)
}


// Copy over the gatling classes to the app/classes folder
tasks.register("copyGatlingToAppDir", Copy::class) {
    dependsOn("gatlingClasses")
    from("build/classes/kotlin/gatling")
    into("build/extra-directory/app/classes/")
}

// Copy over the gatling resources folder
tasks.register("copyGatlingResources", Copy::class) {
    from("src/gatling/resources")
    into("build/extra-directory/app/resources")
}

tasks.named("jib") {
    dependsOn("copyGatlingToAppDir", "copyGatlingResources")
}

tasks.named("jibDockerBuild") {
    dependsOn("copyGatlingToAppDir", "copyGatlingResources")
}

tasks.named("jibBuildTar") {
    dependsOn("appDir", "copyGatlingResources")
}

// Configuration, which should be used by Jib
val gatlingJibDocker: Configuration by configurations.creating {
    extendsFrom(
        configurations.kotlinCompilerPluginClasspathGatling.get(),
        configurations.gatling.get(),
    )
}

jib {
    configurationName.set("gatlingJibDocker")
    extraDirectories.setPaths("build/extra-directory")
    container {
        mainClass = "io.gatling.app.Gatling"
        args = listOf("-s", "todos.TodosSimulation")
        jvmFlags = listOf("--add-opens", "java.base/java.lang=ALL-UNNAMED", "-rf", "/tmp/gatling-results")
    }
}
