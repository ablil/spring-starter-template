@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
rootProject.name = "spring-starter-template"
include("app", "api")
