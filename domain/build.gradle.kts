plugins {
    alias(libs.plugins.jetbrains.kotlin.serialization)
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "home.felipe.domain"
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.kotlin.reflect)
    implementation(libs.dagger)
    implementation(libs.gson)
    kapt(libs.dagger.compiler)

    coreLibraryDesugaring(libs.android.desugar)

    implementation(libs.gson)

    // test
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    kaptTest(libs.dagger.compiler)
}