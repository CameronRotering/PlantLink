pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "PlantLInk"
include(
    ":app",
    "vico",
    "vico:compose",
    "vico:compose-m3",
    "vico:core",
)
 