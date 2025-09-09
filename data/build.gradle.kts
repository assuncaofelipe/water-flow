plugins {
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    id("com.android.library")
    id("com.google.devtools.ksp")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    androidResources { noCompress += "tflite" }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.processor.annotations.jvm)

    coreLibraryDesugaring(libs.android.desugar)

    // Room (se este módulo persistir algo)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    // (ou ksp(libs.room.compiler))

    // Hilt
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    // (ou ksp(libs.hilt.compiler))
    implementation(libs.timber)

    // Coroutines / JSON
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.gson)

    // TFLite / CSV / Charts
    implementation(libs.tensorflow.lite)
    implementation(libs.kotlin.csv)
    implementation(libs.vico.core)
    implementation(libs.vico.compose)

    // TFLite play services
    // implementation(libs.play.services.tflite.java)
    // implementation(libs.play.services.tflite.support)
    // implementation(libs.play.services.tflite.gpu)

    // Testes
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}