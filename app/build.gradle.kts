plugins {
    alias(libs.plugins.jetbrains.kotlin.serialization)
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.jetbrains.kotlin.compose)
}

android {
    namespace = "home.felipe.water.pocket.analysis"
    compileSdk = 34

    defaultConfig {
        applicationId = "home.felipe.water.pocket.analysis"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    coreLibraryDesugaring(libs.android.desugar)

    implementation(project(":data"))
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

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Hilt (runtime + compiler)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Coroutines / JSON
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)

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