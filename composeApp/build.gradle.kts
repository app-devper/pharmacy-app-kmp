@file:Suppress("DEPRECATION") // androidTarget() is flagged for AGP 9; we're on AGP 8.13 and the new
                              // com.android.kotlin.multiplatform.library plugin is library-only,
                              // not for application modules. Re-evaluate when AGP 9 lands.

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    id("pharmacy.architecture.audit")
}

kotlin {
    // jvmToolchain configures both the JDK used to compile and the bytecode
    // target for every JVM-flavoured target (Android + Desktop). One line
    // replaces the per-target jvmTarget plumbing.
    jvmToolchain(17)

    androidTarget()
    jvm()

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { target ->
        target.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("composeApp")
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:domain"))
            implementation(project(":core:ui"))
            implementation(project(":core:data"))
            implementation(project(":features:help"))
            implementation(project(":features:profile"))
            implementation(project(":features:planning"))
            implementation(project(":features:labels"))
            implementation(project(":features:offlinesync"))
            implementation(project(":features:saleshistory"))
            implementation(project(":features:expiry"))
            implementation(project(":features:auth"))
            implementation(project(":features:movements"))
            implementation(project(":features:bulkimport"))
            implementation(project(":features:stockcount"))
            implementation(project(":features:customers"))
            implementation(project(":features:suppliers"))
            implementation(project(":features:imports"))
            implementation(project(":features:users"))
            implementation(project(":features:reports"))
            implementation(project(":features:ky"))
            implementation(project(":features:stock"))
            implementation(project(":features:sell"))
            implementation(project(":features:settings"))

            implementation(compose.runtime)
            implementation(compose.material3)
            implementation(libs.compose.ui.tooling.preview)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation.compose)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.ktor.client.core)

            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.multiplatform.settings)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.java)
        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.kotlinx.browser)
            implementation(npm("@js-joda/core", "5.6.0"))
            implementation(npm("@js-joda/timezone", "2.18.2"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.koin.core)
        }
    }
}

android {
    namespace = "app.devper.pharm"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "app.devper.pharm"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.app.versionCode.get().toInt()
        versionName = libs.versions.app.version.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

// Generated Res class is referenced from common code (e.g. Typography.kt) —
// pin the package so the import path is stable regardless of module renames.
compose.resources {
    generateResClass = org.jetbrains.compose.resources.ResourcesExtension.ResourceClassGeneration.Never
}

compose.desktop {
    application {
        mainClass = "app.devper.pharm.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "PharmacyApp"
            // macOS / Windows installers require MAJOR > 0 (no 0.x). Keep
            // installer version separate from the in-app version string so we
            // can keep iterating in 0.x without breaking native packaging.
            packageVersion = "1.0.0"
        }
    }
}
