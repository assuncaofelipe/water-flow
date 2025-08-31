// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.projectDir.resolve("build"))
}

tasks.register("runTests") {
    dependsOn(
        ":app:testDebugUnitTest",
        ":domain:testDebugUnitTest",
        ":data:testDevDebugUnitTest"
    )
}
