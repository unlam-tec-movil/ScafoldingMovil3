import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ksp)
    alias(libs.plugins.daggerHiltAndroid)
    alias(libs.plugins.kover)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.kotlin.compose.compiler)
    id("com.google.gms.google-services")
    //  id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.2.10"
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        languageVersion.set(KotlinVersion.KOTLIN_2_1)
        apiVersion.set(KotlinVersion.KOTLIN_2_1)
    }
}

android {
    namespace = "ar.edu.unlam.mobile.scaffolding"
    compileSdk = 36

    defaultConfig {
        applicationId = "ar.edu.unlam.mobile.scaffolding"
        minSdk = 30
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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // FIREBASE
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))

    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // ========== BASE ANDROID ==========
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    // ========== COMPOSE ==========
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icon)

    // ========== NAVIGATION ==========
    implementation(libs.androidx.navigation.compose)

    // ========== DAGGER HILT ==========
    implementation(libs.google.dagger.hilt.android)
    implementation(libs.androidx.compose.ui.text)
    ksp(libs.google.dagger.hilt.android.compiler)
    implementation(libs.google.dagger.hilt.android.testing)
    implementation(libs.androidx.hilt.navigation.compose)
    androidTestImplementation(libs.google.dagger.hilt.android.testing)
    testImplementation(libs.google.dagger.hilt.android.testing)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.kotlinx.coroutines.android) // o la última

    implementation(libs.coil.compose)
    // ========== GOOGLE MAPS & LOCATION ==========
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.maps.compose)

    // ========== PERSISTENCIA ==========
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)

    // ========== COROUTINES ==========
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)

    // ========== IMAGE LOADING ==========
    implementation(libs.coil.compose)

    // ========== ACCOMPANIST ==========
    implementation(libs.accompanist.permissions)
    // ========== TESTING ==========
    testImplementation(libs.junit)
    testImplementation(libs.google.dagger.hilt.android.testing)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.google.dagger.hilt.android.testing)

    // ========== DEBUG ==========
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // splash
    implementation(libs.androidx.core.splashscreen.v101)
}
