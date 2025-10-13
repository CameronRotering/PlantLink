// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.library") version "8.13.0" apply false
    id("com.google.devtools.ksp") version "2.2.20-2.0.2" apply false // Most recent version that works with this version of kotlin

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
}