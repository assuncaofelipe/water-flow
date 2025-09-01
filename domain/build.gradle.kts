plugins {
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.android.library")
    kotlin("kapt")
}

android {
    namespace = "home.felipe.domain"
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
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.reflect)
    implementation(libs.kotlinx.coroutines.android)

    // Dagger (se usar no domínio)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    // (ou ksp(libs.dagger.compiler) se migrar)

    implementation(libs.gson)
    coreLibraryDesugaring(libs.android.desugar)

    // Test
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}