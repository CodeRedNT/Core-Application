// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.hilt) apply false
    id("jacoco")
}

subprojects {
    configurations.all {
        resolutionStrategy.dependencySubstitution {
            substitute(module("br.com.coderednt:common")).using(project(":common"))
            substitute(module("br.com.coderednt:ui")).using(project(":ui"))
            substitute(module("br.com.coderednt:monitoring")).using(project(":monitoring"))
            substitute(module("br.com.coderednt:performance")).using(project(":performance"))
        }
    }
}

tasks.register<JacocoReport>("jacocoFullReport") {
    group = "Reporting"
    description = "Gera relatório consolidado de cobertura de todos os módulos"

    val subprojects = listOf("common", "monitoring", "performance", "ui", "app")
    
    val classDirectories = subprojects.map { project ->
        fileTree("${project}/build/tmp/kotlin-classes/debug") {
            include("**/*.class")
            exclude("**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*", "**/*Test*.*", "**/di/**", "**/theme/**")
        }
    }
    
    val sourceDirectories = subprojects.map { project ->
        "${project}/src/main/java"
    }
    
    val executionData = subprojects.map { project ->
        fileTree("${project}/build") {
            include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
            include("jacoco/testDebugUnitTest.exec")
            include("jacoco/testDevDebugUnitTest.exec")
        }
    }

    this.classDirectories.setFrom(files(classDirectories))
    this.sourceDirectories.setFrom(files(sourceDirectories))
    this.executionData.setFrom(files(executionData))

    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}
