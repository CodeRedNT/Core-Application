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
    }
}

rootProject.name = "Core App"
include(":app")
include(":common")
include(":monitoring")
include(":ui")
include(":performance")
include(":database")
include(":datastore")
include(":navigation")
include(":analytics")
include(":architecture")
include(":domain")

// Mapeamento manual dos diretórios
project(":common").projectDir = file("core/common")
project(":monitoring").projectDir = file("core/monitoring")
project(":ui").projectDir = file("core/ui")
project(":performance").projectDir = file("features/performance")
project(":database").projectDir = file("core/database")
project(":datastore").projectDir = file("core/datastore")
project(":navigation").projectDir = file("core/navigation")
project(":analytics").projectDir = file("core/analytics")
project(":architecture").projectDir = file("core/architecture")
project(":domain").projectDir = file("core/domain")
