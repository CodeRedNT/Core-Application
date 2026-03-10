plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "br.com.coderednt.coreapp.core.network"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    
    // Networking
    api(libs.retrofit)
    api(libs.converter.moshi)
    api(libs.okhttp)
    implementation(libs.logging.interceptor)
    
    // JSON
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.kotlin.codegen)
    
    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Internal
    implementation(project(":monitoring"))
    implementation(project(":logging"))

    // Test
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}
