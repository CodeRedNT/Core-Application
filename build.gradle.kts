// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.devtools.ksp) apply false
    alias(libs.plugins.hilt) apply false
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
