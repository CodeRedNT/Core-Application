pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "Core App"
include(":app")
include(":common")
include(":monitoring")
include(":ui")
include(":performance")

// Mapeamento manual dos diretórios para remover a hierarquia lógica do Gradle
project(":common").projectDir = file("core/common")
project(":monitoring").projectDir = file("core/monitoring")
project(":ui").projectDir = file("core/ui")
project(":performance").projectDir = file("features/performance")
