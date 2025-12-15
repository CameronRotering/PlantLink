import org.apache.log4j.BasicConfigurator.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.google.devtools.ksp")

    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.org.jetbrains.kotlin.android)
}

android {
    namespace = "com.themakers.plantlink"
    compileSdk = 36

    configure()

    defaultConfig {
        applicationId = "com.themakers.plantlink"
        minSdk = 31
        //noinspection EditedTargetSdkVersion
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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

    buildFeatures {
        compose = true
    }
    @Suppress("UnstableApiUsage")
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

dependencies {

    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.ktx)


    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    androidTestImplementation(libs.androidx.junit)
    testImplementation(libs.junit)

    implementation(libs.androidx.material.icons.extended.android)

    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compiler)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui.android)
    implementation(libs.androidx.ui.tooling.preview)

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.material)
}