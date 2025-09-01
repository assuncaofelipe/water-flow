plugins {
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    id("com.android.library")
    kotlin("kapt")
}

android {
    namespace = "home.felipe.data"
    compileSdk = 36

    defaultConfig {
        minSdk = 29
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)

    coreLibraryDesugaring(libs.android.desugar)

    // Room (se este módulo persistir algo)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)
    // (ou ksp(libs.room.compiler))

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // (ou ksp(libs.hilt.compiler))

    // Coroutines / JSON
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.gson)

    // TFLite / CSV / Charts
    implementation(libs.tensorflow.lite)
    implementation(libs.kotlin.csv)
    implementation(libs.vico.core)
    implementation(libs.vico.compose)

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}