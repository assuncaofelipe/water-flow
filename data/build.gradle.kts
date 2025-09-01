plugins {
    alias(libs.plugins.jetbrains.kotlin.serialization)
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.jetbrains.kotlin.compose)
}

android {
    namespace = "home.felipe.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 29
        targetSdk = 34

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

    // Core / Lifecycle / Activity
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose (via BOM)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.ui.tooling)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    coreLibraryDesugaring(libs.android.desugar)

    // room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Hilt (runtime + compiler)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Coroutines / JSON
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

    // Gson
    implementation(libs.gson)

    // TensorFlow Lite
    implementation(libs.tensorflow.lite)

    // CSV
    implementation(libs.kotlin.csv)

    // Charts (Vico)
    implementation(libs.vico.core)
    implementation(libs.vico.compose)

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.test.manifest)
}