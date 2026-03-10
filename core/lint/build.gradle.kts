import org.gradle.api.tasks.bundling.Jar

plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.android.lint.api)
    testImplementation(libs.android.lint.api)
    testImplementation(libs.android.lint.tests)
    testImplementation(libs.junit)
}

tasks.named<Jar>("jar") {
    manifest {
        attributes["Lint-Registry-V2"] = "br.com.coderednt.coreapp.core.lint.CoreIssueRegistry"
    }
}
